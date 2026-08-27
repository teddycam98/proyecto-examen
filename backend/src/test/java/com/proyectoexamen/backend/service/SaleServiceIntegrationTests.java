package com.proyectoexamen.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.proyectoexamen.backend.dto.SaleForm;
import com.proyectoexamen.backend.dto.TransactionItemForm;
import com.proyectoexamen.backend.exception.BusinessException;
import com.proyectoexamen.backend.entity.Category;
import com.proyectoexamen.backend.entity.Product;
import com.proyectoexamen.backend.entity.Sale;
import com.proyectoexamen.backend.entity.UnitOfMeasure;
import com.proyectoexamen.backend.repository.CategoryRepository;
import com.proyectoexamen.backend.repository.ProductRepository;
import com.proyectoexamen.backend.repository.StockMovementRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SaleServiceIntegrationTests {

    @Autowired private SaleService saleService;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private StockMovementRepository movementRepository;

    @Test
    void saleDecreasesStockAndCreatesMovement() {
        Product product = createProduct(5);
        Sale sale = saleService.create(saleForm(product.getId(), 3));

        assertThat(sale.getTotal()).isEqualByComparingTo("7.50");
        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(2);
        assertThat(movementRepository.count()).isEqualTo(1);
    }

    @Test
    void saleRejectsInsufficientStock() {
        Product product = createProduct(2);

        assertThatThrownBy(() -> saleService.create(saleForm(product.getId(), 3)))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Stock insuficiente");
        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(2);
    }

    private Product createProduct(int stock) {
        Category category = new Category();
        category.setName("Prueba " + System.nanoTime());
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setCode("TEST-" + System.nanoTime());
        product.setName("Producto de prueba");
        product.setUnitOfMeasure(UnitOfMeasure.UNIDAD);
        product.setPurchasePrice(new BigDecimal("1.00"));
        product.setSalePrice(new BigDecimal("2.50"));
        product.setStock(stock);
        product.setMinimumStock(1);
        product.setCategory(category);
        return productRepository.save(product);
    }

    private SaleForm saleForm(Long productId, int quantity) {
        SaleForm form = new SaleForm();
        TransactionItemForm item = form.getItems().getFirst();
        item.setProductId(productId);
        item.setQuantity(quantity);
        return form;
    }
}
