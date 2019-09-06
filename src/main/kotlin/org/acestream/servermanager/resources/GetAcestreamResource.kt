package org.acestream.servermanager.resources

import org.acestream.servermanager.domain.AcestreamUrlDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URL

@RestController
@RequestMapping("getacestream")
class GetAcestreamResource {

    val regex = """acestream://([a-zA-Z0-9]+)""".toRegex()

    @GetMapping
    fun getAceStream(@RequestParam("url") url: String): AcestreamUrlDto {
        try {
            val body = URL(url).readText()
            val matchResult = regex.find(body)
            return AcestreamUrlDto(acestream = matchResult?.value)
        } catch (e: Exception) {
            return AcestreamUrlDto(acestream = null)
        }
    }
}