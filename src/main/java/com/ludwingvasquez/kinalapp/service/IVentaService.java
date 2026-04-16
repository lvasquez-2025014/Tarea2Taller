package com.ludwingvasquez.kinalapp.service;

import com.ludwingvasquez.kinalapp.entity.Venta;
import java.util.List;
import java.util.Optional;

public interface IVentaService {
    List<Venta> listarTodos();
    Venta guardar(Venta venta);
    Optional<Venta> buscarCV(Long codigoVenta);
    Venta actualizar(Long codigoVenta, Venta venta);
    void eliminar(Long codigoVenta);
    boolean existePorCV(Long codigoVenta);
    List<Venta> listarActivas();

    // Métodos de estadísticas
    long contarVentasPorCliente(String dpiCliente);
    double calcularTotalVentasPorCliente(String dpiCliente);
    int contarTotalVentas();
    double calcularIngresosTotales();
    List<Venta> listarVentasPorCliente(String dpiCliente);
}