package az.edu.ada.wm2.lab6.service;

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

    @InjectMocks
    private ProductServiceImpl productService;

    private UUID productId;
    private Product product;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        product = new Product(productId, "Milk", BigDecimal.TEN, LocalDate.now().plusDays(5));
    }

    @Test
    void createProductAssignsIdAndSaves() {
        Product newProduct = new Product();
        newProduct.setProductName("Bread");
        newProduct.setPrice(BigDecimal.valueOf(2));
        newProduct.setExpirationDate(LocalDate.now().plusDays(3));

        when(productRepository.save(newProduct)).thenReturn(newProduct);

        Product result = productService.createProduct(newProduct);

        assertNotNull(result.getId());
        verify(productRepository).save(newProduct);
    }

    @Test
    void getProductByIdReturnsEntity() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(productId);

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

        List<Product> result = productService.getAllProducts();

        assertEquals(1, result.size());
    }

    @Test
    void updateProductSavesExistingProduct() {
        Product updated = new Product();
        updated.setProductName("Updated Milk");
        updated.setPrice(BigDecimal.valueOf(12));
        updated.setExpirationDate(LocalDate.now().plusDays(7));

        when(productRepository.existsById(productId)).thenReturn(true);
        when(productRepository.save(updated)).thenReturn(updated);

        Product result = productService.updateProduct(productId, updated);

        assertEquals(productId, result.getId());
        assertEquals("Updated Milk", result.getProductName());
    }

    @Test
    void deleteProductDeletesExistingEntity() {
        when(productRepository.existsById(productId)).thenReturn(true);

        productService.deleteProduct(productId);

        verify(productRepository).deleteById(productId);
    }

    @Test
    void getProductsExpiringBeforeDelegatesToRepository() {
        LocalDate cutoff = LocalDate.now().plusDays(10);
        when(productRepository.findByExpirationDateBefore(cutoff)).thenReturn(List.of(product));

        List<Product> result = productService.getProductsExpiringBefore(cutoff);

        assertEquals(1, result.size());
    }

    @Test
    void getProductsByPriceRangeDelegatesToRepository() {
        BigDecimal min = BigDecimal.ZERO;
        BigDecimal max = BigDecimal.TEN;
        when(productRepository.findByPriceBetween(min, max)).thenReturn(List.of(product));

        List<Product> result = productService.getProductsByPriceRange(min, max);

        assertEquals(1, result.size());
    }
}
