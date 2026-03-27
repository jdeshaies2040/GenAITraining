package com.example.genaitraining.domain.repository

import com.example.genaitraining.domain.model.Lift
import com.example.genaitraining.domain.model.LiftStatus
import com.example.genaitraining.domain.model.Trail
import com.example.genaitraining.domain.model.TrailStatus
import kotlinx.coroutines.flow.Flow

interface SnowtoothRepository {
    suspend fun getLifts(): List<Lift>
    suspend fun getTrails(): List<Trail>
    suspend fun updateLiftStatus(id: String, status: LiftStatus): Lift?
    suspend fun updateTrailStatus(id: String, status: TrailStatus): Trail?
    fun observeLiftStatus(): Flow<Lift?>
    fun observeTrailStatus(): Flow<Trail?>
}
