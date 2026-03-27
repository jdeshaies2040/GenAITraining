package com.example.genaitraining.domain.model

data class Lift(
    val id: String,
    val name: String,
    val status: LiftStatus,
    val capacity: Int,
    val night: Boolean,
    val elevationGain: Int
)

enum class LiftStatus {
    OPEN, CLOSED, HOLD
}
