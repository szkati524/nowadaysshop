package catalog_service.infrastructure.adapters.in.web;

import catalog_service.application.ports.in.ProductUseCase;
import catalog_service.domain.model.Product;
import catalog_service.infrastructure.adapters.in.web.dto.CreateProductRequest;
import catalog_service.infrastructure.adapters.in.web.dto.ReserveStockRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                request.initialStock()
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
}
