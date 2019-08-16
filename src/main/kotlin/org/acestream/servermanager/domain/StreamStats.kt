package org.acestream.servermanager.domain

data class StreamStats(
    val status: String,
    val speed_down: Int,
    val speed_up: Int,
    val uploaded: Long,
    val downloaded: Long,
    val playback_session_id: String?,
    val peers: Int,
    val total_progress: Int
)