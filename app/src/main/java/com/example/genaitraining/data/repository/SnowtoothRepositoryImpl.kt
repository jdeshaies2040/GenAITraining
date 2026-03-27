package com.example.genaitraining.data.repository

import com.apollographql.apollo.ApolloClient
import com.example.genaitraining.GetAllLiftsQuery
import com.example.genaitraining.GetAllTrailsQuery
import com.example.genaitraining.LiftStatusChangeSubscription
import com.example.genaitraining.SetLiftStatusMutation
import com.example.genaitraining.SetTrailStatusMutation
import com.example.genaitraining.TrailStatusChangeSubscription
import com.example.genaitraining.domain.model.Lift
import com.example.genaitraining.domain.model.LiftStatus
import com.example.genaitraining.domain.model.Trail
import com.example.genaitraining.domain.model.TrailStatus
import com.example.genaitraining.domain.repository.SnowtoothRepository
import com.example.genaitraining.type.LiftStatus as GraphQLLiftStatus
import com.example.genaitraining.type.TrailStatus as GraphQLTrailStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SnowtoothRepositoryImpl @Inject constructor(
    private val apolloClient: ApolloClient
) : SnowtoothRepository {

    override suspend fun getLifts(): List<Lift> {
        val response = apolloClient.query(GetAllLiftsQuery()).execute()
        return response.data?.allLifts?.map {
            Lift(
                id = it.id,
                name = it.name,
                status = LiftStatus.valueOf(it.status?.name ?: "CLOSED"),
                capacity = it.capacity,
                night = it.night,
                elevationGain = it.elevationGain
            )
        } ?: emptyList()
    }

    override suspend fun getTrails(): List<Trail> {
        val response = apolloClient.query(GetAllTrailsQuery()).execute()
        return response.data?.allTrails?.map {
            Trail(
                id = it.id,
                name = it.name,
                status = TrailStatus.valueOf(it.status?.name ?: "CLOSED"),
                difficulty = it.difficulty,
                groomed = it.groomed,
                trees = it.trees,
                night = it.night
            )
        } ?: emptyList()
    }

    override suspend fun updateLiftStatus(id: String, status: LiftStatus): Lift? {
        val response = apolloClient.mutation(
            SetLiftStatusMutation(id, GraphQLLiftStatus.valueOf(status.name))
        ).execute()
        return response.data?.setLiftStatus?.let {
            Lift(it.id, "", LiftStatus.valueOf(it.status?.name ?: "CLOSED"), 0, false, 0)
        }
    }

    override suspend fun updateTrailStatus(id: String, status: TrailStatus): Trail? {
        val response = apolloClient.mutation(
            SetTrailStatusMutation(id, GraphQLTrailStatus.valueOf(status.name))
        ).execute()
        return response.data?.setTrailStatus?.let {
            Trail(it.id, "", TrailStatus.valueOf(it.status?.name ?: "CLOSED"), "", false, false, false)
        }
    }

    override fun observeLiftStatus(): Flow<Lift?> {
        return apolloClient.subscription(LiftStatusChangeSubscription()).toFlow().map { response ->
            response.data?.liftStatusChange?.let {
                Lift(it.id, "", LiftStatus.valueOf(it.status?.name ?: "CLOSED"), 0, false, 0)
            }
        }
    }

    override fun observeTrailStatus(): Flow<Trail?> {
        return apolloClient.subscription(TrailStatusChangeSubscription()).toFlow().map { response ->
            response.data?.trailStatusChange?.let {
                Trail(it.id, "", TrailStatus.valueOf(it.status?.name ?: "CLOSED"), "", false, false, false)
            }
        }
    }
}
