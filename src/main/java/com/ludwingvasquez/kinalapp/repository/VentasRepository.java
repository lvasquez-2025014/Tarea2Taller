package com.ludwingvasquez.kinalapp.repository;

import com.ludwingvasquez.kinalapp.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VentasRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByEstado(Long estado);

}