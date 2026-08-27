package com.proyectoexamen.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class SaleForm {

    @Size(max = 120)
    private String customerName = "Publico general";

    @Pattern(regexp = "^$|^[0-9]{8,11}$", message = "Ingrese un DNI o RUC valido")
    private String customerDocument;

    private List<@Valid TransactionItemForm> items = new ArrayList<>();

    public SaleForm() { items.add(new TransactionItemForm()); }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerDocument() { return customerDocument; }
    public void setCustomerDocument(String customerDocument) { this.customerDocument = customerDocument; }
    public List<TransactionItemForm> getItems() { return items; }
    public void setItems(List<TransactionItemForm> items) { this.items = items; }
}
