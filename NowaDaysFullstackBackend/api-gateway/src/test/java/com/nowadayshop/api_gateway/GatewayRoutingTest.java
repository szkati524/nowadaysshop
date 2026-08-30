package com.nowadayshop.api_gateway;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false"
        }
)
@AutoConfigureWebTestClient
class GatewayRoutingTest {

    @RegisterExtension
    static WireMockExtension catalogService = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.cloud.gateway.routes[0].id", () -> "catalog-service");
        registry.add("spring.cloud.gateway.routes[0].uri", catalogService::baseUrl);
        registry.add("spring.cloud.gateway.routes[0].predicates[0]", () -> "Path=/api/products/**");
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test

    void shouldRouteToCatalogServiceTest() {
        catalogService.stubFor(get(urlEqualTo("/api/products/123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": \"123\", \"name\": \"Test Product\"}")));

        webTestClient.get()
                .uri("/api/products/123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Test Product");
    }
}