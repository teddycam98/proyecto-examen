package com.proyectoexamen.backend.dto;

import com.proyectoexamen.backend.entity.MovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class StockAdjustmentForm {
    @NotNull(message = "Seleccione un producto")
    private Long productId;

    @NotNull(message = "Seleccione el tipo de ajuste")
    private MovementType movementType = MovementType.AJUSTE_ENTRADA;

    @NotNull(message = "Ingrese una cantidad")
    @Min(value = 1, message = "La cantidad debe ser mayor que cero")
    private Integer quantity;

    @NotBlank(message = "Indique el motivo del ajuste")
    @Size(max = 250)
    private String notes;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
