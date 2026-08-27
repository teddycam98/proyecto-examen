package com.proyectoexamen.backend.dto;

import java.math.BigDecimal;

public class TransactionItemForm {
    private Long productId;
    private Integer quantity = 1;
    private BigDecimal unitPrice;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
