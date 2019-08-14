package org.acestream.servermanager.resources

import org.acestream.servermanager.utils.CommandLineHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("servers")
class ServersResource(
    @Autowired val cliHelper: CommandLineHelper
) {

    @GetMapping("start")
    fun startAcestreamServer(): String {
        return cliHelper.startAcestreamServer()
    }

}