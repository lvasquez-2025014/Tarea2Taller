package com.ludwingvasquez.kinalapp.repository;

import com.ludwingvasquez.kinalapp.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

    List<DetalleVenta> findByEstado(Long estado);
    List<DetalleVenta> findByVenta_CodigoVenta(Integer codigoVenta);
}