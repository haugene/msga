package org.acestream.servermanager.routes

import org.apache.camel.spring.SpringRouteBuilder
import org.springframework.stereotype.Component

@Component
class RequestProxyRoute : SpringRouteBuilder() {
    override fun configure() {
        from("servlet:request?servletName=RequestProxyServlet")
            .routeId("request-proxy")
            .process {
                if (!it.`in`.getHeader("url", String::class.java).startsWith(prefix = "http")) {
                    throw IllegalArgumentException("Proxy URL must start with \"http\"")
                }
            }
            .toD("\${header.url}?bridgeEndpoint=true")
            .process { it.`in`.headers["Access-Control-Allow-Origin"] = "*" }
    }
}