package org.acestream.servermanager.resources

import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

data class StreamDataDto (
    val url: String
)

@RestController
@RequestMapping("stream")
@CrossOrigin(origins = ["*"])
class StreamResource {

    var currentStream: String? = null

    @GetMapping
    fun redirectToStream(request: HttpServletRequest, response: HttpServletResponse) {
        if (currentStream == null) {
            response.sendError(404)
            return
        }
        val splitRequestUrl = request.requestURL.toString().split(':')
        val splitStreamUrl = currentStream!!.split(":")
        val generatedStreamUrl = "${splitRequestUrl[0]}:${splitRequestUrl[1]}:${splitStreamUrl[2]}"
       response.sendRedirect(generatedStreamUrl)
    }

    @PutMapping
    fun setStream(@RequestBody stream: StreamDataDto) {
        currentStream = stream.url
    }
}