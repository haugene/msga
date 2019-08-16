package org.acestream.servermanager.domain

data class StreamTestResult(
    val streamId: String,
    val isLive: Boolean,
    val startedPlaying: Boolean,
    val averageDownloadSpeed: Int,
    val averageUploadSpeed: Int,
    val averageNumberOfPeers: Int,
    val maxNumberOfPeers: Int,
    val failed: Boolean = false,
    val errorMessage: String? = null
)