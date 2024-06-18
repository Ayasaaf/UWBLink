package fr.eya.uwblink.ui.home

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.uwb.RangingPosition
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import fr.eya.ranging.EndpointEvents
import fr.eya.ranging.UwbEndPoint
import fr.eya.uwblink.Save.saveTextFile
import fr.eya.uwblink.storage.EndpointData
import fr.eya.uwblink.storage.convertTimestampToReadableDate
import fr.eya.uwblink.storage.readDataFromFile
import fr.eya.uwblink.storage.saveJsonAsTextFile
import fr.eya.uwblink.storage.writeDataToFile
import fr.eya.uwblink.uwbranging.data.UwbRangingControlSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    uwbRangingControlSource: UwbRangingControlSource,
    private val context: Context
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiStateImpl(listOf(), listOf(), false))
    private val endpoints = mutableListOf<UwbEndPoint>()
    private val endpointPositions = mutableMapOf<UwbEndPoint, RangingPosition>()

    private var isRanging = false

    private var isDataStorageActive = false
    private var storagePeriod = 0

    private var storageJob: Job? = null

    private val _ConnectedEndpointDATA = MutableLiveData<List<EndpointData>?>()
    val endpointData: MutableLiveData<List<EndpointData>?> get() = _ConnectedEndpointDATA

    private fun updateUiState(): HomeUiState {
        val uiState = HomeUiStateImpl(
            endpoints
                .mapNotNull { endpoint ->
                    endpointPositions[endpoint]?.let { position ->
                        ConnectedEndpoint(endpoint, position)
                    }
                }.toList(),
            endpoints.filter { !endpointPositions.containsKey(it) }.toList(),
            isRanging
        )
        return uiState
    }

    val uiState = _uiState.asStateFlow()

    init {
        uwbRangingControlSource
            .observeRangingResults()
            .onEach { result ->
                try {
                    when (result) {
                        is EndpointEvents.EndpointFound -> {
                            endpoints.add(result.endpoint)
                            Log.d("HomeViewModel", "Endpoint Found: ${result.endpoint}")
                        }

                        is EndpointEvents.UwbDisconnected -> {
                            endpointPositions.remove(result.endpoint)
                            Log.d("HomeViewModel", "UwbDisconnected: ${result.endpoint}")
                        }

                        is EndpointEvents.PositionUpdated -> {
                            endpointPositions[result.endpoint] = result.position
                            if (isDataStorageActive) {
                                saveEndpointData(context, result.endpoint, result.position)
                            }
                        }

                        is EndpointEvents.EndpointLost -> {
                            endpoints.remove(result.endpoint)
                            endpointPositions.remove(result.endpoint)
                            Log.d("HomeViewModel", "Endpoint Lost: ${result.endpoint}")
                        }

                        else -> return@onEach
                    }
                    _uiState.update { updateUiState() }
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Error processing ranging result: $result", e)
                }
            }
            .launchIn(viewModelScope)

        uwbRangingControlSource.isRunning
            .onEach { running ->
                try {
                    isRanging = running
                    if (!running) {
                        endpoints.clear()
                        endpointPositions.clear()
                    }
                    _uiState.update { updateUiState() }
                    Log.d("HomeViewModel", "Ranging state updated: isRanging = $isRanging")
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Error updating ranging state", e)
                }
            }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    private fun saveEndpointData(
        context: Context,
        endpoint: UwbEndPoint,
        position: RangingPosition
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val endpointData = EndpointData(
                EndPointId = endpoint.id,
                Distance = position.distance?.value ?: 0.0f,
                Azimuth = position.azimuth?.value ?: 0.0f,
                Elevation = position.elevation?.value,
                Timestamp = System.currentTimeMillis()
            )
            val currentData = _ConnectedEndpointDATA.value?.toMutableList() ?: mutableListOf()
            currentData.add(endpointData)
            _ConnectedEndpointDATA.postValue(currentData)

            val jsonString = Gson().toJson(currentData)
            writeDataToFile(context, jsonString, "endpoint_data.json")
              // Convertir les données en texte brut
            val textContent = currentData.joinToString("\n") { data ->
                """
                EndPointId: ${data.EndPointId}
                Distance: ${data.Distance}
                Azimuth: ${data.Azimuth}
                Elevation: ${data.Elevation}
                Timestamp: ${convertTimestampToReadableDate(data.Timestamp)}
                """.trimIndent()
            }
            // Enregistrer le fichier texte
            val fileSaved = saveTextFile(context, "endpoint_data.txt", textContent)
            if (fileSaved) {
                Log.d("HomeViewModel", "Text file saved successfully")
            } else {
                Log.e("HomeViewModel", "Failed to save text file")
            }
        }
    }

    fun startSavingDataPeriodically(period: Int, context: Context) {
        if (!isDataStorageActive && period > 0) {
            isDataStorageActive = true
            storagePeriod = period * 1000
            Toast.makeText(context, "Storing started", Toast.LENGTH_SHORT).show()
            storageJob = viewModelScope.launch {
                delay(storagePeriod.toLong())
                stopSavingDataPeriodically(context)
            }
        }
    }

    fun stopSavingDataPeriodically(context: Context) {
        isDataStorageActive = false
        storageJob?.cancel()
        Toast.makeText(context, "Storing finished", Toast.LENGTH_SHORT).show()
    }

    fun loadEndpointData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = readDataFromFile(context, "endpoint_data.json")
            _ConnectedEndpointDATA.postValue(data)
        }
    }




    private data class HomeUiStateImpl(
        override val connectedEndpoints: List<ConnectedEndpoint>,
        override val disconnectedEndpoints: List<UwbEndPoint>,
        override val isRanging: Boolean,
    ) : HomeUiState

    companion object {
        fun provideFactory(
            context: Context,
            uwbRangingControlSource: UwbRangingControlSource,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(uwbRangingControlSource, context) as T
                }
            }
    }

    fun saveDataToTextFile(context: Context, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = _ConnectedEndpointDATA.value
            if (!data.isNullOrEmpty()) {
                val jsonString = Gson().toJson(data)
                val fileSaved = saveJsonAsTextFile(context, "$fileName.txt", jsonString)
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post {
                    if (fileSaved) {
                        Log.d("HomeViewModel", "Text file saved successfully")
                        Toast.makeText(context, "Text file saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("HomeViewModel", "Failed to save text file")
                        Toast.makeText(context, "Failed to save text file", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Log.w("HomeViewModel", "No data to save")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "No data to save", Toast.LENGTH_SHORT).show()
                }
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