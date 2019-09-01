package org.acestream.servermanager.services

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.*
import org.acestream.servermanager.domain.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant.now
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.core.MediaType

@Service
class StreamTestService {

    val aceBase = "http://localhost:30000/ace/getstream"

    private val streamTests: MutableMap<Int, StreamTest> = mutableMapOf()
    private var currentTest: Pair<Int, Job?> = Pair(0, null)

    companion object {
        private val logger = LoggerFactory.getLogger(StreamTestService::class.java)
        private val objectMapper =
            jacksonObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }


    fun testStreams(streamTestRequestDto: StreamTestRequestDto): Int {
        val testId = streamTests.size + 1
        val streamTest = StreamTest(
            testId = testId,
            timePerStream = streamTestRequestDto.timePerStream,
            streamIds = streamTestRequestDto.streamIds,
            streamTestResults = mutableListOf()
        )

        streamTests[testId] = streamTest

        cancelOngoingTests(testId)
        val job =GlobalScope.launch {
            testStreams(streamTest)
        }

        currentTest = Pair(testId, job)
        return testId
    }

    private fun cancelOngoingTests(testId: Int) {
        val currentTestJobId = currentTest.first
        val currentTestJob = currentTest.second

        if (currentTestJob != null && currentTestJob.isActive) {
            logger.info("Test number $currentTestJobId is already running, cancelling it to start new test (#$testId)")
            currentTestJob.cancel()
            streamTests[currentTestJobId]!!.cancelled = true
        }
    }

    private fun testStreams(test: StreamTest) {
        runBlocking {

            for (streamId in test.streamIds) {
                logger.info("Testing Acestream id $streamId")

                val streamResponse = requestStream(streamId)
                if (streamResponse.response == null) {
                    streamFailed(streamId, test, streamResponse)
                } else {
                    delay(5000L) // Wait 5 seconds before starting to poll stats
                    test.streamTestResults.add(collectTestResults(test, streamId, streamResponse.response))
                }

                logger.info("Completed testing of:  $streamId")
                test.completed = true
            }

        }
    }

    private fun collectTestResults(test: StreamTest, streamId: String, streamInfo: StreamInfo): StreamTestResult {

        val start = now()
        val statuses = mutableListOf<String>()
        val downloadSpeeds = mutableListOf<Int>()
        val uploadSpeeds = mutableListOf<Int>()
        val numberOfPeers = mutableListOf<Int>()

        while (now().isBefore(start.plusSeconds(test.timePerStream))) {
            Thread.sleep(500)
            val statsResponse = requestStats(streamInfo.stat_url)
            if (statsResponse.response == null) throw Exception() // TODO: Maybe add better error handling here
            val stats = statsResponse.response

            statuses.add(stats.status)
            downloadSpeeds.add(stats.speed_down)
            uploadSpeeds.add(stats.speed_up)
            numberOfPeers.add(stats.peers)
        }

        return StreamTestResult(
            streamId = streamId,
            isLive = streamInfo.is_live,
            startedPlaying = statuses.contains("dl"),
            averageDownloadSpeed = downloadSpeeds.sum() / downloadSpeeds.size,
            averageUploadSpeed = uploadSpeeds.sum() / uploadSpeeds.size,
            averageNumberOfPeers = numberOfPeers.sum() / numberOfPeers.size,
            maxNumberOfPeers = numberOfPeers.max()!!
        )
    }

    private fun streamFailed(
        streamId: String,
        streamTest: StreamTest,
        streamResponse: GetStreamResponse
    ) {
        streamTest.streamTestResults.add(
            StreamTestResult(
                streamId = streamId,
                isLive = false,
                startedPlaying = false,
                averageDownloadSpeed = 0,
                averageUploadSpeed = 0,
                averageNumberOfPeers = 0,
                maxNumberOfPeers = 0,
                failed = true,
                errorMessage = streamResponse.error
            )
        )
    }

    private fun requestStream(streamId: String): GetStreamResponse {
        val rawResponse = ClientBuilder.newClient().target(aceBase)
            .queryParam("format", "json")
            .queryParam("id", streamId)
            .request(MediaType.APPLICATION_JSON)
            .get(String::class.java)
        return objectMapper.readValue(rawResponse)
    }

    private fun requestStats(statsUrl: String): GetStatsResponse {
        val rawResponse = ClientBuilder.newClient().target(statsUrl)
            .request(MediaType.APPLICATION_JSON)
            .get(String::class.java)
        return objectMapper.readValue(rawResponse)
    }

    fun getTests(): Map<Int, StreamTest> = streamTests
}