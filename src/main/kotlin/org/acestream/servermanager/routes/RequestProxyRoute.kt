package org.acestream.servermanager.routes

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.spring.SpringRouteBuilder
import org.springframework.stereotype.Component
import java.lang.RuntimeException

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
            .process(UrlProcessor())
            .toD("\${header.PROXY_TO_URL}?bridgeEndpoint=true")
            .process { it.`in`.headers["Access-Control-Allow-Origin"] = "*" }
    }
}

class UrlProcessor: Processor {
    override fun process(exchange: Exchange?) {
        if (exchange == null) throw RuntimeException()
        exchange.`in`.setHeader("PROXY_TO_URL", exchange.`in`.getHeader(Exchange.HTTP_QUERY, String::class.java).removePrefix("url="))
    }
}