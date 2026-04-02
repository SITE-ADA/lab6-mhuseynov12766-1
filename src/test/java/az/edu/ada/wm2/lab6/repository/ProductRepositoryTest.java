package az.edu.ada.wm2.lab6.repository;

import az.edu.ada.wm2.lab6.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findByExpirationDateBeforeReturnsMatchingProducts() {
        Product milk = new Product("Milk", BigDecimal.TEN, LocalDate.now().plusDays(2));
        Product rice = new Product("Rice", BigDecimal.ONE, LocalDate.now().plusDays(20));

        productRepository.save(milk);
        productRepository.save(rice);

        List<Product> result = productRepository.findByExpirationDateBefore(LocalDate.now().plusDays(5));

        assertEquals(1, result.size());
        assertEquals("Milk", result.getFirst().getProductName());
    }

    @Test
    void findByPriceBetweenReturnsMatchingProducts() {
        Product milk = new Product("Milk", BigDecimal.TEN, LocalDate.now().plusDays(2));
        Product bread = new Product("Bread", BigDecimal.valueOf(3), LocalDate.now().plusDays(1));

        productRepository.save(milk);
        productRepository.save(bread);

        List<Product> result = productRepository.findByPriceBetween(BigDecimal.valueOf(2), BigDecimal.valueOf(5));

        assertEquals(1, result.size());
        assertEquals("Bread", result.getFirst().getProductName());
    }

    @Test
    void findByPriceBetweenReturnsEmptyWhenNoProductsMatch() {
        productRepository.save(new Product("Milk", BigDecimal.TEN, LocalDate.now().plusDays(2)));

        List<Product> result =
                productRepository.findByPriceBetween(BigDecimal.valueOf(100), BigDecimal.valueOf(200));

        assertTrue(result.isEmpty());
    }
}
