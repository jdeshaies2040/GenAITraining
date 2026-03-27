package com.example.genaitraining.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.genaitraining.domain.repository.SnowtoothRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SnowtoothViewModel @Inject constructor(
    private val repository: SnowtoothRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SnowtoothState())
    val state: StateFlow<SnowtoothState> = _state.asStateFlow()

    fun onIntent(intent: SnowtoothIntent) {
        when (intent) {
            is SnowtoothIntent.LoadData -> loadData()
            is SnowtoothIntent.UpdateLiftStatus -> updateLiftStatus(intent.id, intent.status)
            is SnowtoothIntent.UpdateTrailStatus -> updateTrailStatus(intent.id, intent.status)
            is SnowtoothIntent.ObserveStatusChanges -> observeChanges()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val lifts = repository.getLifts()
                val trails = repository.getTrails()
                _state.update { it.copy(lifts = lifts, trails = trails, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun updateLiftStatus(id: String, status: com.example.genaitraining.domain.model.LiftStatus) {
        viewModelScope.launch {
            try {
                repository.updateLiftStatus(id, status)
                // Refresh data or let subscription handle it
                loadData()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun updateTrailStatus(id: String, status: com.example.genaitraining.domain.model.TrailStatus) {
        viewModelScope.launch {
            try {
                repository.updateTrailStatus(id, status)
                loadData()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun observeChanges() {
        viewModelScope.launch {
            repository.observeLiftStatus().collect { updatedLift ->
                updatedLift?.let { lift ->
                    _state.update { s ->
                        s.copy(lifts = s.lifts.map { if (it.id == lift.id) it.copy(status = lift.status) else it })
                    }
                }
            }
        }
        viewModelScope.launch {
            repository.observeTrailStatus().collect { updatedTrail ->
                updatedTrail?.let { trail ->
                    _state.update { s ->
                        s.copy(trails = s.trails.map { if (it.id == trail.id) it.copy(status = trail.status) else it })
                    }
                }
            }
        }
    }
}
