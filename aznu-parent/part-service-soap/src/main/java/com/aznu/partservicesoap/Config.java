package com.aznu.partservicesoap;

import jakarta.xml.ws.Endpoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.config.annotation.EnableWs;


@Configuration
@Slf4j
@EnableWs
public class Config {

    @Autowired
    private PartWebService webService; // your web service component

    @Bean
    public ServletRegistrationBean wsDispatcherServlet() {
        CXFServlet cxfServlet = new CXFServlet();
        return new ServletRegistrationBean(cxfServlet, "/repair/*");
    }

    @Bean(name="cxf")
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public Endpoint helloWorldEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), webService);
        endpoint.publish("part");
        return endpoint;
    }
}
