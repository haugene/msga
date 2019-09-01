package org.acestream.servermanager.domain

data class StreamTest(
    val testId: Int,
    val timePerStream: Long,
    val streamIds: List<String>,
    val streamTestResults: MutableList<StreamTestResult>,
    var completed: Boolean = false,
    var cancelled: Boolean = false
)