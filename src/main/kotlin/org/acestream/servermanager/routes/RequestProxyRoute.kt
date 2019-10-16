package org.acestream.servermanager.routes

import org.apache.camel.spring.SpringRouteBuilder
import org.springframework.stereotype.Component

@Component
class RequestProxyRoute : SpringRouteBuilder() {

    val proxyUrlProcessor = ProxyUrlProcessor()

    override fun configure() {
        from("servlet:request?servletName=RequestProxyServlet")
            .routeId("request-proxy")
            .process(proxyUrlProcessor)
            .toD("\${header.PROXY_TO_URL}?bridgeEndpoint=true")
            .process { it.`in`.headers["Access-Control-Allow-Origin"] = "*" }
    }
}
