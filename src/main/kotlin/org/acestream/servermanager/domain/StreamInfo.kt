package org.acestream.servermanager.domain

data class StreamInfo(
    val stat_url: String,
    val playback_session_id: String?,
    val command_url: String,
    val is_live: Boolean,
    val playback_url: String
)