package com.ludwingvasquez.kinalapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "detalles_ventas")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo_detalle")
    @JsonProperty("codigoDetalle")
    private Integer codigoDetalle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codigo_venta")
    @JsonIgnoreProperties("detalles")
    private Venta venta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codigo_producto")
    @JsonProperty("producto")
    private Producto producto;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "precio_unitario")
    @JsonProperty("precioUnitario")
    private Double precioUnitario;

    @Column(name = "estado")
    private Long estado;

    public DetalleVenta() {
    }

    public DetalleVenta(Venta venta, Producto producto, Integer cantidad, Double precioUnitario, Long estado) {
        this.venta = venta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.estado = estado;
    }


    public Integer getCodigoDetalle() {
        return codigoDetalle;
    }

    public void setCodigoDetalle(Integer codigoDetalle) {
        this.codigoDetalle = codigoDetalle;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Long getEstado() {
        return estado;
    }

    public void setEstado(Long estado) {
        this.estado = estado;
    }
}