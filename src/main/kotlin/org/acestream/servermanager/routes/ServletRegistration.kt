package org.acestream.servermanager.routes

import org.apache.camel.component.servlet.CamelHttpTransportServlet
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class ServletRegistration {

    @Bean
    fun acestreamProxyServletRegistrationBean(): ServletRegistrationBean<CamelHttpTransportServlet> {
        val servlet = ServletRegistrationBean(CamelHttpTransportServlet(), "/aceproxy" + "/*")
        servlet.setName("AcestreamProxyServlet")
        return servlet
    }

    @Bean
    fun requestProxyServletRegistrationBean(): ServletRegistrationBean<CamelHttpTransportServlet> {
        val servlet = ServletRegistrationBean(CamelHttpTransportServlet(), "/proxy" + "/*")
        servlet.setName("RequestProxyServlet")
        return servlet
    }
}