package com.example.genaitraining.domain.model

data class Trail(
    val id: String,
    val name: String,
    val status: TrailStatus,
    val difficulty: String,
    val groomed: Boolean,
    val trees: Boolean,
    val night: Boolean
)

enum class TrailStatus {
    OPEN, CLOSED
}
