package com.example.genaitraining.ui

import com.example.genaitraining.domain.model.Lift
import com.example.genaitraining.domain.model.LiftStatus
import com.example.genaitraining.domain.model.Trail
import com.example.genaitraining.domain.model.TrailStatus

data class SnowtoothState(
    val lifts: List<Lift> = emptyList(),
    val trails: List<Trail> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class SnowtoothIntent {
    object LoadData : SnowtoothIntent()
    data class UpdateLiftStatus(val id: String, val status: LiftStatus) : SnowtoothIntent()
    data class UpdateTrailStatus(val id: String, val status: TrailStatus) : SnowtoothIntent()
    object ObserveStatusChanges : SnowtoothIntent()
}
