package org.acestream.servermanager.resources

import org.acestream.servermanager.utils.CommandLineHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("openvpn")
class OpenVPNResource(
    @Autowired val cliHelper: CommandLineHelper
) {

    @GetMapping("start")
    fun startOpenvpn() {
        return cliHelper.startOpenvpn()
    }

    @GetMapping("logs")
    fun logs(): String {
        return cliHelper.openvpnLogs()
    }
}