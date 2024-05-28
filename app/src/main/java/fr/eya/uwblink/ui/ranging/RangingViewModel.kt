package fr.eya.uwblink.ui.ranging

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.eya.uwblink.uwbranging.data.UwbRangingControlSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class RangingViewModel(private val uwbRangingControlSource: UwbRangingControlSource) : ViewModel() {

    private val _uiState: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val uiState = _uiState.asStateFlow()

    init {
        uwbRangingControlSource.isRunning.onEach {
            Log.d("rangingRun", "isRunning state updated: $it")
            _uiState.update { it } }.launchIn(viewModelScope)
    }

    fun startRanging() {
        Log.d("RangingStart", "Starting ranging")
        uwbRangingControlSource.start()
    }

    fun stopRanging() {
        Log.d("RangingStop", "Stopping ranging")
        uwbRangingControlSource.stop()
    }

    companion object {
        fun provideFactory(
            uwbRangingControlSource: UwbRangingControlSource,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RangingViewModel(uwbRangingControlSource) as T
                }
            }
    }
}
