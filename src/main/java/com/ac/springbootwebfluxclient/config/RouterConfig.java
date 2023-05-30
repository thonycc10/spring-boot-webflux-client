package com.ac.springbootwebfluxclient.config;

import com.ac.springbootwebfluxclient.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> rute(ProductHandler productHandler) {
        return route(GET("/api/client"), productHandler::list)
                .andRoute(GET("/api/client/{id}"), productHandler::view)
                .andRoute(POST("/api/client"), productHandler::create)
                .andRoute(PUT("/api/client/{id}"), productHandler::edit)
                .andRoute(DELETE("/api/client/{id}"), productHandler::delete)
                .andRoute(DELETE("/api/client/upload/{id}"), productHandler::upload);
    }
}
