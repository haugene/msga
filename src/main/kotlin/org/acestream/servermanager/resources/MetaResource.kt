package org.acestream.servermanager.resources

import org.acestream.servermanager.domain.MetaInfoDto
import org.acestream.servermanager.utils.CommandLineHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("meta")
class MetaResource(
    @Autowired val cliHelper: CommandLineHelper
) {

    @GetMapping("info")
    fun metaInfo(): MetaInfoDto {
        return MetaInfoDto(
            cliHelper.getJavaVersion(),
            cliHelper.getOpenVpnVersion(),
            cliHelper.getAcestreamVersion()
        )
    }

    @GetMapping("testDns")
    fun testDns() {
        return cliHelper.testDns()
    }

    @GetMapping("publicIp")
    fun publicIp(): String {
        return cliHelper.publicIp()!!
    }
}