package com.proyectoexamen.backend.entity;

public enum MovementType {
    COMPRA("Compra", true),
    VENTA("Venta", false),
    AJUSTE_ENTRADA("Ajuste de entrada", true),
    AJUSTE_SALIDA("Ajuste de salida", false),
    ANULACION_VENTA("Anulacion de venta", true),
    ANULACION_COMPRA("Anulacion de compra", false);

    private final String label;
    private final boolean incoming;

    MovementType(String label, boolean incoming) {
        this.label = label;
        this.incoming = incoming;
    }

    public String getLabel() { return label; }
    public boolean isIncoming() { return incoming; }
}
