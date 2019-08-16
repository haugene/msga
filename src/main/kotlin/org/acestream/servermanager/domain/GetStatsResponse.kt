package org.acestream.servermanager.domain

data class GetStatsResponse(
    val response: StreamStats?,
    val error: String?
)