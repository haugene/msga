package org.acestream.servermanager.resources

import org.acestream.servermanager.domain.AcestreamUrlDto
import org.springframework.web.bind.annotation.*
import java.net.URL

@RestController
@RequestMapping("getacestream")
@CrossOrigin(origins = ["*"])
class GetAcestreamResource {

    val regex = """[a-fA-F0-9]{40}""".toRegex()

    @GetMapping
    fun getAceStream(@RequestParam("url") url: String): List<AcestreamUrlDto> {
        try {
            println("reading $url");
            val body = URL(url).readText()
            return regex.findAll(body).map { AcestreamUrlDto(acestream = it.value) }.toList()
        } catch (e: Exception) {
            println(e.toString());
        }
        return emptyList()
    }
}