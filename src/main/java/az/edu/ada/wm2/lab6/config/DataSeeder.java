package az.edu.ada.wm2.lab6.config;

import az.edu.ada.wm2.lab6.model.Category;
import az.edu.ada.wm2.lab6.model.Product;
import az.edu.ada.wm2.lab6.repository.CategoryRepository;
import az.edu.ada.wm2.lab6.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataSeeder(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (categoryRepository.count() > 0 || productRepository.count() > 0) {
            return;
        }

        Category dairy = new Category("Dairy");
        Category bakery = new Category("Bakery");
        Category beverages = new Category("Beverages");

        categoryRepository.saveAll(List.of(dairy, bakery, beverages));

        Product milk = new Product("Milk", BigDecimal.valueOf(2.50), LocalDate.now().plusDays(7));
        milk.addCategory(dairy);

        Product bread = new Product("Bread", BigDecimal.valueOf(1.80), LocalDate.now().plusDays(3));
        bread.addCategory(bakery);

        Product orangeJuice = new Product("Orange Juice", BigDecimal.valueOf(3.20), LocalDate.now().plusDays(14));
        orangeJuice.addCategory(beverages);

        Product yogurt = new Product("Yogurt", BigDecimal.valueOf(1.40), LocalDate.now().plusDays(10));
        yogurt.addCategory(dairy);

        productRepository.saveAll(List.of(milk, bread, orangeJuice, yogurt));
    }
}
