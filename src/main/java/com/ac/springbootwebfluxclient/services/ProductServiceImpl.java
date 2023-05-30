package com.ac.springbootwebfluxclient.services;

import com.ac.springbootwebfluxclient.model.Product;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Service
public class ProductServiceImpl implements ProductService {

    private final WebClient webClient;

    public ProductServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<Product> findAll() {
        return webClient.get() // el config ya tiene mapeado el url
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Product.class);
//                .exchangeToFlux(clientResponse ->  clientResponse.bodyToFlux(Product.class));
    }

    @Override
    public Mono<Product> findById(String id) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        return webClient.get()// el config ya tiene mapeado el url
                .uri("/{id}", params.get("id"))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve() // otra forma para convertir
                .bodyToMono(Product.class);
//                .exchangeToMono(clientResponse ->  clientResponse.bodyToMono(Product.class));
    }

    @Override
    public Mono<Product> save(Product product) {
        return webClient.post()
                .accept(MediaType.APPLICATION_JSON) // acepta response
                .contentType(MediaType.APPLICATION_JSON) // enviaas
                .body(fromValue(product))
                .retrieve()
                .bodyToMono(Product.class);
    }

    @Override
    public Mono<Product> update(Product product, String id) {
        return webClient.put()
                .uri("/{id}", Collections.singletonMap("id", id))
                .accept(MediaType.APPLICATION_JSON) // acepta response
                .contentType(MediaType.APPLICATION_JSON) // enviaas
                .body(fromValue(product))
                .retrieve()
                .bodyToMono(Product.class);
    }

    @Override
    public Mono<Void> delete(String id) {
        return webClient.delete()
                .uri("/{id}", Collections.singletonMap("id", id))
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Product> update(FilePart file, String id) {
        MultipartBodyBuilder parts = new MultipartBodyBuilder();
        parts.asyncPart("file", file.content(), DataBuffer.class).headers(h -> {
            h.setContentDispositionFormData("file", file.filename());
        });

        return webClient.post()
                .uri("/upload/{id}", Collections.singletonMap("id", id))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(parts.build())
                .retrieve()
                .bodyToMono(Product.class);
    }
}
