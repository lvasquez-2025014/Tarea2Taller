package com.ludwingvasquez.kinalapp.repository;

import com.ludwingvasquez.kinalapp.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
//
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByEstado(Long estado);
}
