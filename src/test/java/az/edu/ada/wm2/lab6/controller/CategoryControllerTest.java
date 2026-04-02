package az.edu.ada.wm2.lab6.controller;

import az.edu.ada.wm2.lab6.model.dto.CategoryRequestDto;
import az.edu.ada.wm2.lab6.model.dto.CategoryResponseDto;
import az.edu.ada.wm2.lab6.model.dto.ProductResponseDto;
import az.edu.ada.wm2.lab6.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    void createReturnsCreatedCategory() {
        CategoryRequestDto requestDto = new CategoryRequestDto("Dairy");
        CategoryResponseDto responseDto = new CategoryResponseDto(UUID.randomUUID(), "Dairy");

        when(categoryService.create(requestDto)).thenReturn(responseDto);

        ResponseEntity<CategoryResponseDto> response = categoryController.create(requestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void getProductsReturnsCategoryProducts() {
        UUID categoryId = UUID.randomUUID();
        ProductResponseDto product = new ProductResponseDto(
                UUID.randomUUID(),
                "Cheese",
                BigDecimal.valueOf(8),
                LocalDate.of(2026, 4, 12),
                List.of("Dairy")
        );

        when(categoryService.getProducts(categoryId)).thenReturn(List.of(product));

        ResponseEntity<List<ProductResponseDto>> response = categoryController.getProducts(categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cheese", response.getBody().getFirst().getProductName());
    }
}
