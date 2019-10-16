package org.acestream.servermanager.resources

import org.acestream.servermanager.domain.StreamTest
import org.acestream.servermanager.domain.StreamTestRequestDto
import org.acestream.servermanager.services.StreamTestService
import org.acestream.servermanager.utils.CommandLineHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.ws.rs.core.MediaType

@RestController
@RequestMapping("acestream")
@CrossOrigin(origins = ["*"])
class AcestreamResource(
    @Autowired val cliHelper: CommandLineHelper,
    @Autowired val streamTestService: StreamTestService
) {

    @GetMapping("start")
    fun startAcestreamServer(): String {
        return cliHelper.startAcestreamServer()
    }

    @PostMapping("testStreams")
    fun testStreams(@RequestBody streamTestRequestDto: StreamTestRequestDto): Map<String, *> {
        return mapOf(Pair("testId", streamTestService.testStreams(streamTestRequestDto)))
    }

    @GetMapping("tests", produces = [MediaType.APPLICATION_JSON])
    fun getTestResults(): Map<Int, StreamTest> {
        return streamTestService.getTests()
    }

}