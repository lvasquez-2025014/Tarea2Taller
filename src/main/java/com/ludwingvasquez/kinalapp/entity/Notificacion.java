package com.ludwingvasquez.kinalapp.entity;

import java.time.LocalDateTime;

public class Notificacion {
    private Long id;
    private String tipo;
    private String titulo;
    private String mensaje;
    private LocalDateTime fecha;
    private boolean leida;
    private String icono;
    private String color;

    public Notificacion() {}

    public Notificacion(Long id, String tipo, String titulo, String mensaje, LocalDateTime fecha, boolean leida, String icono, String color) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.leida = leida;
        this.icono = icono;
        this.color = color;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
