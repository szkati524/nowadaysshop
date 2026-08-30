package catalog_service.persistence.in.web;

import catalog_service.config.security.JwtAuthenticationFilter;
import catalog_service.config.security.JwtUtils;
import catalog_service.config.security.SecurityConfig;
import catalog_service.infrastructure.adapters.in.web.ProductController;
import catalog_service.infrastructure.adapters.in.web.exception.GlobalExceptionHandler;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import catalog_service.application.ports.in.ProductUseCase;
import catalog_service.domain.exception.ProductNotFoundException;
import catalog_service.domain.model.Product;
import catalog_service.infrastructure.adapters.in.web.dto.CreateProductRequest;
import catalog_service.infrastructure.adapters.in.web.dto.ReserveStockRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductUseCase productUseCase;

    @MockBean
    private JwtUtils jwtUtils;

    private Product sampleProduct;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        sampleProduct = new Product(
                productId,
                "Słuchawki BT",
                "Słuchawki bezprzewodowe z ANC",
                new BigDecimal("499.00"),
                30
        );
    }



    @Test

    void shouldGetAllProductsWithoutAuthenticationTest() throws Exception {
        when(productUseCase.getAllProduct()).thenReturn(List.of(sampleProduct));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(productId.toString()))
                .andExpect(jsonPath("$[0].name").value("Słuchawki BT"));
    }

    @Test

    void shouldGetProductByIdWithoutAuthenticationTest() throws Exception {
        when(productUseCase.getProductById(productId)).thenReturn(sampleProduct);

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value("Słuchawki BT"));
    }

    @Test

    void shouldReturn404WhenProductNotFoundTest() throws Exception {
        when(productUseCase.getProductById(productId))
                .thenThrow(new ProductNotFoundException("Nie znaleziono produktu"));

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isNotFound());
    }



    @Test

    void shouldReturn403WhenCreatingProductWithoutAuthTest() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Laptop", "Opis", new BigDecimal("3000.00"), 5);

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")

    void shouldReturn403WhenCreatingProductAsUserRoleTest() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Laptop", "Opis", new BigDecimal("3000.00"), 5);

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")

    void shouldCreateProductAsAdminTest() throws Exception {
        CreateProductRequest request = new CreateProductRequest("Laptop", "Opis", new BigDecimal("3000.00"), 5);
        when(productUseCase.createProduct(any(), any(), any(), any())).thenReturn(sampleProduct);

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(productId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")

    void shouldReturn400WhenCreateProductRequestIsInvalidTest() throws Exception {
        CreateProductRequest invalidRequest = new CreateProductRequest("", "Opis", new BigDecimal("-100.00"), -5);

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(productUseCase, never()).createProduct(any(), any(), any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")

    void shouldReserveStockAsAdminTest() throws Exception {
        ReserveStockRequest request = new ReserveStockRequest(5);

        mockMvc.perform(post("/api/products/{id}/reserve", productId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(productUseCase, times(1)).reserveStock(eq(productId), eq(5));
    }
}

