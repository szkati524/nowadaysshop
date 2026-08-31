package catalog_service.infrastructure.adapters.in.web;

import catalog_service.application.ports.in.ProductUseCase;
import catalog_service.application.ports.out.ProductRepositoryPort;
import catalog_service.domain.model.Product;
import catalog_service.infrastructure.adapters.in.web.dto.CreateProductRequest;
import catalog_service.infrastructure.adapters.in.web.dto.PagedResult;
import catalog_service.infrastructure.adapters.in.web.dto.ProductSearchQuery;
import catalog_service.infrastructure.adapters.in.web.dto.ReserveStockRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductUseCase productUseCase;


    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;

    }
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody CreateProductRequest request){
        Product createdProduct = productUseCase.createProduct(
                request.name(),
                request.description(),
                request.price(),
                request.initialStock(),
                request.category()
        );
       return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }
    @GetMapping
    public ResponseEntity<List<Product>> getAllProduct(){
        return ResponseEntity.ok(productUseCase.getAllProduct());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id){
        return ResponseEntity.ok(productUseCase.getProductById(id));

    }
    @PostMapping("/{id}/reserve")
    public ResponseEntity<Void> reserveStock(@PathVariable UUID id, @Valid @RequestBody ReserveStockRequest request){
        productUseCase.reserveStock(id,request.quantity());
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{id}/add-stock")
    public ResponseEntity<Void> addStock(@PathVariable UUID id,@Valid @RequestBody ReserveStockRequest request){
        productUseCase.addStock(id,request.quantity());
        return ResponseEntity.ok().build();
    }
    @GetMapping("/search")
    public ResponseEntity<PagedResult<Product>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        ProductSearchQuery query = new ProductSearchQuery(
                name, category, minPrice, maxPrice, page, size, sortBy, sortDirection);
        PagedResult<Product> result = productUseCase.searchProducts(query);
        return ResponseEntity.ok(result);
    }

}
