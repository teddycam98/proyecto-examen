package com.proyectoexamen.backend.entity;

public enum TransactionStatus {
    COMPLETADA("Completada"),
    ANULADA("Anulada");

    private final String label;
    TransactionStatus(String label) { this.label = label; }
    public String getLabel() { return label; }
}
