package az.edu.ada.wm2.lab6.service;

import az.edu.ada.wm2.lab6.model.Category;
import az.edu.ada.wm2.lab6.model.Product;
import az.edu.ada.wm2.lab6.model.dto.ProductRequestDto;
import az.edu.ada.wm2.lab6.model.dto.ProductResponseDto;
import az.edu.ada.wm2.lab6.model.mapper.ProductMapper;
import az.edu.ada.wm2.lab6.repository.CategoryRepository;
import az.edu.ada.wm2.lab6.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProductMapper productMapper
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto product) {
        Product entity = productMapper.toEntity(product);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        assignCategories(entity, product.getCategoryIds());
        return productMapper.toResponseDto(productRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(UUID id) {
        return productMapper.toResponseDto(getExistingProduct(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(UUID id, ProductRequestDto product) {
        Product existingProduct = getExistingProduct(id);
        existingProduct.setProductName(product.getProductName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setExpirationDate(product.getExpirationDate());
        syncCategories(existingProduct, product.getCategoryIds());
        return productMapper.toResponseDto(productRepository.save(existingProduct));
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = getExistingProduct(id);
        syncCategories(product, List.of());
        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsExpiringBefore(LocalDate date) {
        return productRepository.findByExpirationDateBefore(date)
                .stream()
                .map(productMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(productMapper::toResponseDto)
                .toList();
    }

    private Product getExistingProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    private void assignCategories(Product product, List<UUID> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            product.setCategories(new HashSet<>());
            return;
        }

        Set<Category> categories = loadCategories(categoryIds);
        product.setCategories(new HashSet<>());
        categories.forEach(product::addCategory);
    }

    private void syncCategories(Product product, List<UUID> categoryIds) {
        Set<Category> currentCategories = new HashSet<>(product.getCategories());
        currentCategories.forEach(product::removeCategory);
        assignCategories(product, categoryIds);
    }

    private Set<Category> loadCategories(List<UUID> categoryIds) {
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new RuntimeException("One or more categories not found");
        }
        return new HashSet<>(categories);
    }
}
