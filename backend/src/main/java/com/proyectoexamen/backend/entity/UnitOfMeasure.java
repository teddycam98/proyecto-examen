package com.proyectoexamen.backend.entity;

public enum UnitOfMeasure {
    UNIDAD("Unidad"),
    PAQUETE("Paquete"),
    CAJA("Caja"),
    DOCENA("Docena"),
    JUEGO("Juego");

    private final String label;

    UnitOfMeasure(String label) { this.label = label; }
    public String getLabel() { return label; }
}
