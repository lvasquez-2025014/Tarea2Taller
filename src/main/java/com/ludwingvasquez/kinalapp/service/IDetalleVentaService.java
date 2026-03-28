package com.ludwingvasquez.kinalapp.service;

import com.ludwingvasquez.kinalapp.entity.DetalleVenta;

import java.util.List;
import java.util.Optional;

public interface IDetalleVentaService {
    List<DetalleVenta> listarTodos();
    DetalleVenta guardar(DetalleVenta detalleVenta);
    Optional<DetalleVenta> buscarPorId(Integer codigoDetalle);
    DetalleVenta actualizar(Integer codigoDetalle, DetalleVenta detalleVenta);
    void eliminar(Integer codigoDetalle);
    boolean existePorId(Integer codigoDetalle);
    List<DetalleVenta> listarPorVenta(Integer codigoVenta);
}