package az.edu.ada.wm2.lab6.controller;

import az.edu.ada.wm2.lab6.model.dto.CategoryRequestDto;
import az.edu.ada.wm2.lab6.model.dto.CategoryResponseDto;
import az.edu.ada.wm2.lab6.model.dto.ProductResponseDto;
import az.edu.ada.wm2.lab6.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> create(@RequestBody CategoryRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @PostMapping("/{categoryId}/products")
    public ResponseEntity<CategoryResponseDto> addProduct(
            @PathVariable UUID categoryId,
            @RequestParam UUID productId
    ) {
        try {
            return ResponseEntity.ok(categoryService.addProduct(categoryId, productId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{categoryId}/products")
    public ResponseEntity<List<ProductResponseDto>> getProducts(@PathVariable UUID categoryId) {
        try {
            return ResponseEntity.ok(categoryService.getProducts(categoryId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
