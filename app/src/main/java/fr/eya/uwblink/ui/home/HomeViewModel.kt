package fr.eya.uwblink.ui.home

import androidx.core.uwb.RangingPosition
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.eya.ranging.EndpointEvents
import fr.eya.ranging.UwbEndPoint
import fr.eya.uwblink.uwbranging.data.UwbRangingControlSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class HomeViewModel(uwbRangingControlSource: UwbRangingControlSource) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiStateImpl(listOf(), listOf(), false))
    private val endpoints = mutableListOf<UwbEndPoint>()
    private val endpointPositions = mutableMapOf<UwbEndPoint, RangingPosition>()

    private var isRanging = false
    private fun updateUiState(): HomeUiState {
        return HomeUiStateImpl(
            endpoints
                .mapNotNull { endpoint ->
              endpointPositions[endpoint]?.let {
                  position ->
                    ConnectedEndpoint(
                        endpoint,
                        position
                    )
                }
            }.toList(),
            endpoints.filter { !endpointPositions.containsKey(it) }.toList(),
            isRanging
        )
    }

    val uiState = _uiState.asStateFlow()

    init {
        uwbRangingControlSource
            .observeRangingResults()
            .onEach { result ->
                when (result) {
                    is EndpointEvents.EndpointFound -> endpoints.add(result.endpoint)
                    is EndpointEvents.UwbDisconnected -> endpointPositions.remove(result.endpoint)
                    is EndpointEvents.PositionUpdated -> endpointPositions[result.endpoint] =
                        result.position

                    is EndpointEvents.EndpointLost -> {
                        endpoints.remove(result.endpoint)

                        endpointPositions.remove(result.endpoint)
                    }

                    else -> return@onEach
                }
                _uiState.update { updateUiState() }
            }
            .launchIn(viewModelScope)

        uwbRangingControlSource.isRunning
            .onEach { running ->
                isRanging = running
                if (!running) {
                    endpoints.clear()
                    endpointPositions.clear()
                }
                _uiState.update { updateUiState() }
            }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    private data class HomeUiStateImpl(
        override val connectedEndpoints: List<ConnectedEndpoint>,
        override val disconnectedEndpoints: List<UwbEndPoint>,
        override val isRanging: Boolean,
    ) : HomeUiState

    companion object {
        fun provideFactory(
            uwbRangingControlSource: UwbRangingControlSource
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(uwbRangingControlSource) as T
                }
            }
    }
}

interface HomeUiState {
    val connectedEndpoints: List<ConnectedEndpoint>
    val disconnectedEndpoints: List<UwbEndPoint>
    val isRanging: Boolean
}

data class ConnectedEndpoint(val endpoint: UwbEndPoint, val position: RangingPosition)