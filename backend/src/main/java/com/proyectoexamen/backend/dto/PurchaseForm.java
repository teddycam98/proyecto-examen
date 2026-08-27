package com.proyectoexamen.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class PurchaseForm {

    @NotNull(message = "Seleccione un proveedor")
    private Long supplierId;

    @Size(max = 40)
    private String documentReference;

    private List<@Valid TransactionItemForm> items = new ArrayList<>();

    public PurchaseForm() { items.add(new TransactionItemForm()); }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getDocumentReference() { return documentReference; }
    public void setDocumentReference(String documentReference) { this.documentReference = documentReference; }
    public List<TransactionItemForm> getItems() { return items; }
    public void setItems(List<TransactionItemForm> items) { this.items = items; }
}
