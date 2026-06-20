package com.hotelreservation.gateway;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    CommandLineRunner warmUpLoadBalancers(DiscoveryClient discoveryClient) {
        return args -> {
            WebClient client = WebClient.create();
            List.of("user-service", "hotel-service", "reservation-service",
                            "payment-service", "notification-service")
                    .forEach(service -> {
                        try {
                            List<ServiceInstance> instances = discoveryClient.getInstances(service);
                            if (!instances.isEmpty()) {
                                client.get()
                                        .uri(instances.get(0).getUri() + "/actuator/health")
                                        .retrieve()
                                        .toBodilessEntity()
                                        .block(Duration.ofSeconds(5));
                            }
                        } catch (Exception ignored) {}
                    });
        };
    }
}
