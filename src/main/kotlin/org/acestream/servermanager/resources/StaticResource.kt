package org.acestream.servermanager.resources

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping


@Controller
class StaticController {

    @RequestMapping(value = ["/", "/settings", "/home", "/setup", "/acestream", "/vpn"])
    fun index(): String {
        return "index.html"
    }
}