package az.edu.ada.wm2.lab6.service;

import az.edu.ada.wm2.lab6.model.dto.ProductRequestDto;
import az.edu.ada.wm2.lab6.model.dto.ProductResponseDto;
import az.edu.ada.wm2.lab6.model.mapper.ProductMapper;
import az.edu.ada.wm2.lab6.repository.CategoryRepository;
import az.edu.ada.wm2.lab6.model.Product;
import az.edu.ada.wm2.lab6.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private UUID productId;
    private Product product;
    private ProductResponseDto responseDto;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        product = new Product(productId, "Milk", BigDecimal.TEN, LocalDate.now().plusDays(5));
        responseDto = new ProductResponseDto(productId, "Milk", BigDecimal.TEN, product.getExpirationDate(), List.of());
    }

    @Test
    void createProductAssignsIdAndSaves() {
        ProductRequestDto requestDto = new ProductRequestDto("Bread", BigDecimal.valueOf(2), LocalDate.now().plusDays(3), null);
        Product mappedProduct = new Product();
        mappedProduct.setProductName("Bread");
        mappedProduct.setPrice(BigDecimal.valueOf(2));
        mappedProduct.setExpirationDate(requestDto.getExpirationDate());
        ProductResponseDto mappedResponse = new ProductResponseDto(
                productId,
                "Bread",
                BigDecimal.valueOf(2),
                requestDto.getExpirationDate(),
                List.of()
        );

        when(productMapper.toEntity(requestDto)).thenReturn(mappedProduct);
        when(productRepository.save(mappedProduct)).thenReturn(mappedProduct);
        when(productMapper.toResponseDto(mappedProduct)).thenReturn(mappedResponse);

        ProductResponseDto result = productService.createProduct(requestDto);

        assertNotNull(result.getId());
        verify(productRepository).save(mappedProduct);
    }

    @Test
    void getProductByIdReturnsEntity() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDto(product)).thenReturn(responseDto);

        ProductResponseDto result = productService.getProductById(productId);

        assertEquals(productId, result.getId());
    }

    @Test
    void getProductByIdThrowsWhenMissing() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getProductById(productId));
    }

    @Test
    void getAllProductsReturnsRepositoryResults() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toResponseDto(product)).thenReturn(responseDto);

        List<ProductResponseDto> result = productService.getAllProducts();

        assertEquals(1, result.size());
    }

    @Test
    void updateProductSavesExistingProduct() {
        ProductRequestDto updated = new ProductRequestDto(
                "Updated Milk",
                BigDecimal.valueOf(12),
                LocalDate.now().plusDays(7),
                null
        );
        ProductResponseDto updatedResponse = new ProductResponseDto(
                productId,
                "Updated Milk",
                BigDecimal.valueOf(12),
                updated.getExpirationDate(),
                List.of()
        );

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDto(product)).thenReturn(updatedResponse);

        ProductResponseDto result = productService.updateProduct(productId, updated);

        assertEquals(productId, result.getId());
        assertEquals("Updated Milk", result.getProductName());
    }

    @Test
    void deleteProductDeletesExistingEntity() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.deleteProduct(productId);

        verify(productRepository).delete(product);
    }

    @Test
    void getProductsExpiringBeforeDelegatesToRepository() {
        LocalDate cutoff = LocalDate.now().plusDays(10);
        when(productRepository.findByExpirationDateBefore(cutoff)).thenReturn(List.of(product));
        when(productMapper.toResponseDto(product)).thenReturn(responseDto);

        List<ProductResponseDto> result = productService.getProductsExpiringBefore(cutoff);

        assertEquals(1, result.size());
    }

    @Test
    void getProductsByPriceRangeDelegatesToRepository() {
        BigDecimal min = BigDecimal.ZERO;
        BigDecimal max = BigDecimal.TEN;
        when(productRepository.findByPriceBetween(min, max)).thenReturn(List.of(product));
        when(productMapper.toResponseDto(product)).thenReturn(responseDto);

        List<ProductResponseDto> result = productService.getProductsByPriceRange(min, max);

        assertEquals(1, result.size());
    }
}
