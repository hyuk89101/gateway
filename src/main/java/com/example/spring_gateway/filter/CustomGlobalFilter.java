package com.example.spring_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    private final WebClient.Builder webClientBuilder;

    public CustomGlobalFilter(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("====================== CustomGlobalFilter =========================== ");

        /*
        * api 도메인이 아닌 인증 도메인으로 클라이언트에서 접근 시 아래 default 인증기능 예외처리
        * */
        if(exchange.getRequest().getURI().toString().indexOf("/auth") > 0) {
            System.out.println("====================== CustomGlobalFilter Exception Auth =========================== ");

            // 단순히 바로 다음으로 리턴
            return chain.filter(exchange);
        }

        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8013/auth/message")
                /*.header("Authorization", exchange.getRequest().getHeaders().getFirst("Authorization"))*/
                .header("ActionUrl", exchange.getRequest().getURI().toString())
                .header("third-request", "third-service-requestHeader")
                .retrieve()
                .toBodilessEntity()
                .flatMap(response -> {
                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return -100;
    }
}