package org.acestream.servermanager.domain

data class StreamTest(
    val testId: Int,
    val timePerStream: Long,
    val streamIds: List<String>,
    val streamTestResults: MutableList<StreamTestResult>
)