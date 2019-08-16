package org.acestream.servermanager.domain

data class GetStreamResponse(
    val response: StreamInfo?,
    val error: String?
)