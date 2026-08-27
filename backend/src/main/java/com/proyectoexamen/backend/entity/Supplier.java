package com.proyectoexamen.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "suppliers", uniqueConstraints = @UniqueConstraint(name = "uk_supplier_document", columnNames = "document_number"))
public class Supplier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La razon social es obligatoria")
    @Size(max = 120)
    @Column(name = "business_name", nullable = false, length = 120)
    private String businessName;

    @Pattern(regexp = "^$|^[0-9]{8,11}$", message = "Ingrese un DNI o RUC valido")
    @Column(name = "document_number", length = 11)
    private String documentNumber;

    @Size(max = 100)
    @Column(name = "contact_name", length = 100)
    private String contactName;

    @Pattern(regexp = "^$|^[0-9+() -]{7,20}$", message = "Ingrese un telefono valido")
    @Column(length = 20)
    private String phone;

    @Email(message = "Ingrese un correo valido")
    @Size(max = 120)
    @Column(length = 120)
    private String email;

    @Size(max = 200)
    @Column(length = 200)
    private String address;

    @Column(nullable = false)
    private boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = trim(businessName); }
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) {
        String value = trim(documentNumber);
        this.documentNumber = value == null || value.isBlank() ? null : value;
    }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = trim(contactName); }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = trim(phone); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = trim(email); }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = trim(address); }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    private String trim(String value) { return value == null ? null : value.trim(); }
}
