package com.ac.springbootwebfluxclient.handler;

import com.ac.springbootwebfluxclient.model.Product;
import com.ac.springbootwebfluxclient.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.lang.management.MemoryType;
import java.net.URI;
import java.util.Date;

@Component
public class ProductHandler {

    private final ProductService service;

    public ProductHandler(ProductService service) {
        this.service = service;
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Product.class);
    }

    public Mono<ServerResponse> view(ServerRequest request) {
        String id = request.pathVariable("id");

        return service.findById(id).flatMap(p -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(p)
                .switchIfEmpty(ServerResponse.notFound().build()));
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Mono<Product> productMono = request.bodyToMono(Product.class);

        return productMono.flatMap(p -> {
            if (p.getCreateAt() == null) {
                p.setCreateAt(new Date());
            }
            return service.save(p);
        }).flatMap(p -> ServerResponse.created(URI.create("/api/client/".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(p))
                .onErrorResume(error -> {
                    WebClientResponseException errorWebClientResponseException = (WebClientResponseException) error;
                    if (errorWebClientResponseException.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(errorWebClientResponseException.getResponseBodyAsString());
                    }
                    return Mono.error(errorWebClientResponseException);
                });
    }

    public Mono<ServerResponse> edit(ServerRequest request) {
        Mono<Product> productMono = request.bodyToMono(Product.class);
        String id = request.pathVariable("id");

        return productMono.flatMap(p -> ServerResponse.created(URI.create("/api/client/".concat(id)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.update(p, id), Product.class));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return service.delete(id).then(ServerResponse.noContent().build()).switchIfEmpty(ServerResponse.notFound().build());
    }

}
