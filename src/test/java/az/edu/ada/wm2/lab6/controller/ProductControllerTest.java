package az.edu.ada.wm2.lab6.controller;

import az.edu.ada.wm2.lab6.model.dto.ProductRequestDto;
import az.edu.ada.wm2.lab6.model.dto.ProductResponseDto;
import az.edu.ada.wm2.lab6.service.ProductService;
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
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void createProductReturnsCreatedResponse() {
        ProductRequestDto requestDto = new ProductRequestDto(
                "Milk",
                BigDecimal.TEN,
                LocalDate.of(2026, 4, 10),
                List.of()
        );
        ProductResponseDto responseDto = new ProductResponseDto(
                UUID.randomUUID(),
                "Milk",
                BigDecimal.TEN,
                LocalDate.of(2026, 4, 10),
                List.of()
        );

        when(productService.createProduct(requestDto)).thenReturn(responseDto);

        ResponseEntity<ProductResponseDto> response = productController.createProduct(requestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void getProductsByPriceRangeReturnsServiceResults() {
        ProductResponseDto responseDto = new ProductResponseDto(
                UUID.randomUUID(),
                "Bread",
                BigDecimal.valueOf(3),
                LocalDate.of(2026, 4, 5),
                List.of("Food")
        );

        when(productService.getProductsByPriceRange(BigDecimal.valueOf(2), BigDecimal.valueOf(5)))
                .thenReturn(List.of(responseDto));

        ResponseEntity<List<ProductResponseDto>> response =
                productController.getProductsByPriceRange(BigDecimal.valueOf(2), BigDecimal.valueOf(5));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bread", response.getBody().getFirst().getProductName());
    }
}
