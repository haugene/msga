package org.acestream.servermanager.resources

import org.acestream.servermanager.domain.StreamTest
import org.acestream.servermanager.domain.StreamTestRequestDto
import org.acestream.servermanager.services.StreamTestService
import org.acestream.servermanager.utils.CommandLineHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@RestController
@RequestMapping("acestream")
@CrossOrigin(origins = ["*"])
class AcestreamResource(
    @Autowired val cliHelper: CommandLineHelper,
    @Autowired val streamTestService: StreamTestService
) {

    @GetMapping("start")
    fun startAcestreamServer(): String {
        if (cliHelper.acestreamIsRunning()) throw WebApplicationException(Response.Status.CONFLICT)
        return cliHelper.startAcestreamServer()
    }

    @GetMapping("running")
    fun isOpenVpnRunning(): Map<String, Boolean> {
        return mapOf(Pair("running", cliHelper.acestreamIsRunning()))
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