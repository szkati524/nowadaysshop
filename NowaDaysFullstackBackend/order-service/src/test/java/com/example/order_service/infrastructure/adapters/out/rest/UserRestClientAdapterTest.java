package com.example.order_service.infrastructure.adapters.out.rest;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
public class UserRestClientAdapterTest {
    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    private UserRestClientAdapter userAdapter;

    @BeforeEach
    void setUp() {
        userAdapter = new UserRestClientAdapter(
                RestClient.builder(),
                wireMock.getRuntimeInfo().getHttpBaseUrl()
        );

    }

    @Test

    void shouldChargeWalletSuccessfullyTest() {
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("150.50");

        wireMock.stubFor(post(urlEqualTo("/api/users/" + userId + "/wallet/charge"))
                .withRequestBody(equalToJson("{\"amount\": 150.50}"))
                .willReturn(aResponse().withStatus(200)));

        userAdapter.chargeWallet(userId, amount);

        wireMock.verify(postRequestedFor(urlEqualTo("/api/users/" + userId + "/wallet/charge")));
    }

    @Test

    void shouldThrowBadRequestWhenInsufficientFundsTest() {
        UUID userId = UUID.randomUUID();

        wireMock.stubFor(post(urlEqualTo("/api/users/" + userId + "/wallet/charge"))
                .willReturn(aResponse().withStatus(400)));

        assertThatThrownBy(() -> userAdapter.chargeWallet(userId, new BigDecimal("9999.00")))
                .isInstanceOf(HttpClientErrorException.BadRequest.class);
    }
}


