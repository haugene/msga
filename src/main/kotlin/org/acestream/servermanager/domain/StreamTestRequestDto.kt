package org.acestream.servermanager.domain

data class StreamTestRequestDto(
    val timePerStream: Long,
    val streamIds: List<String>
)