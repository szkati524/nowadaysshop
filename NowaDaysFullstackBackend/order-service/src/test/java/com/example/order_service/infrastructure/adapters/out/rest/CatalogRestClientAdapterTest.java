package com.example.order_service.infrastructure.adapters.out.rest;
import com.example.order_service.application.ports.out.dto.ProductResponse;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
public class CatalogRestClientAdapterTest {
    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    private CatalogRestClientAdapter catalogAdapter;

    @BeforeEach
    void setUp() {
        catalogAdapter = new CatalogRestClientAdapter(
                RestClient.builder(),
                wireMock.getRuntimeInfo().getHttpBaseUrl()
        );
    }

    @Test
    @DisplayName("getProduct - Sukces: Poprawna deserializacja JSON do ProductResponse")
    void shouldReturnProductResponseWhenCatalogReturns200Test() {
        UUID productId = UUID.randomUUID();
        String jsonResponseBody = """
                {
                    "id": "%s",
                    "name": "Laptop",
                    "price": 3500.00
                }
                """.formatted(productId);

        wireMock.stubFor(get(urlEqualTo("/api/products/" + productId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponseBody)));

        ProductResponse response = catalogAdapter.getProduct(productId);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(productId);
        assertThat(response.name()).isEqualTo("Laptop");
        assertThat(response.price()).isEqualTo(new BigDecimal("3500.00"));
    }

    @Test
    @DisplayName("getProduct - Obsługa błędu 404: Rzuca HttpClientErrorException.NotFound")
    void shouldThrowNotFoundWhenProductDoesNotExistTest() {
        UUID productId = UUID.randomUUID();

        wireMock.stubFor(get(urlEqualTo("/api/products/" + productId))
                .willReturn(aResponse().withStatus(404)));

        assertThatThrownBy(() -> catalogAdapter.getProduct(productId))
                .isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    @DisplayName("getProduct - Obsługa błędu 500: Rzuca HttpServerErrorException.InternalServerError")
    void shouldThrowServerErrorWhenCatalogReturns500Test() {
        UUID productId = UUID.randomUUID();

        wireMock.stubFor(get(urlEqualTo("/api/products/" + productId))
                .willReturn(aResponse().withStatus(500)));

        assertThatThrownBy(() -> catalogAdapter.getProduct(productId))
                .isInstanceOf(HttpServerErrorException.InternalServerError.class);
    }

    @Test
    @DisplayName("reserveStock - Sukces: Wysyła żądanie POST z ilością i zwraca status 200 OK")
    void shouldReserveStockSuccessfullyTest() {
        UUID productId = UUID.randomUUID();

        wireMock.stubFor(post(urlEqualTo("/api/products/" + productId + "/reserve"))
                .withRequestBody(matchingJsonPath("$.quantity", equalTo("5")))
                .willReturn(aResponse().withStatus(200)));

        catalogAdapter.reserveStock(productId, 5);

        wireMock.verify(postRequestedFor(urlEqualTo("/api/products/" + productId + "/reserve")));
    }
}
