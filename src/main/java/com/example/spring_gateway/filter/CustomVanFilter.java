package com.example.spring_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CustomVanFilter extends AbstractGatewayFilterFactory<CustomVanFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    public CustomVanFilter(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }


    /*
     *
     * VAN 인증서버용
     *
     * */
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {

            System.out.println("====================== CustomVanFilter =========================== ");

            /*
             * api 도메인이 아닌 인증 도메인으로 클라이언트에서 접근 시 아래 default 인증기능 예외처리
             * */
            if(exchange.getRequest().getURI().toString().indexOf("/auth") > 0) {
                System.out.println("====================== CustomVanFilter Exception Auth =========================== ");

                // 단순히 바로 다음으로 리턴
                return chain.filter(exchange);
            }

            return webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8013/auth/message")
                    /*.header("Authorization", exchange.getRequest().getHeaders().getFirst("Authorization"))*/
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> {
                        return chain.filter(exchange);

                    });
        });
    }

    public static class Config {

    }
}
