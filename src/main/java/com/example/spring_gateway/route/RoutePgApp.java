package com.example.spring_gateway.route;

import com.example.spring_gateway.filter.CustomPgFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutePgApp {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder routeLocatorBuilder,
                                      CustomPgFilter customPgFilter) {
        System.out.println("====================== RouteApp =========================== ");
        return routeLocatorBuilder
                .routes()
                .route(r -> r.path("/reseller/**")// 1.path를 확인하고
                        .filters(f -> f // 2.필터를 적용하여
                                .addRequestHeader("AUTH_TEST", "abcdefg")
                                .filter(customPgFilter.apply(new CustomPgFilter.Config()))
                        ).uri("http://localhost:8011"))//uri로 이동시켜준다.

                .route(r -> r.path("/openmarket/**")
                        .filters(f -> f
                                .filter(customPgFilter.apply(new CustomPgFilter.Config()))
                        ).uri("http://localhost:8012"))

                .route(r -> r.path("/auth/**")
                        .filters(f -> f
                                .filter(customPgFilter.apply(new CustomPgFilter.Config()))
                        )
                        .uri("http://localhost:8013"))
                .build();

    }
}
