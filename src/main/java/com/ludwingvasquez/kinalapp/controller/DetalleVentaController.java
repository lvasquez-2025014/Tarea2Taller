package com.ludwingvasquez.kinalapp.controller;

import com.ludwingvasquez.kinalapp.entity.DetalleVenta;
import com.ludwingvasquez.kinalapp.service.IDetalleVentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//
@RestController
@RequestMapping("/detalles_ventas")
public class DetalleVentaController {

    private final IDetalleVentaService detalleVentaService;

    public DetalleVentaController(IDetalleVentaService detalleVentaService) {
        this.detalleVentaService = detalleVentaService;
    }

    @GetMapping
    public ResponseEntity<List<DetalleVenta>> listar() {
        return ResponseEntity.ok(detalleVentaService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody DetalleVenta detalle) {
        try {
            DetalleVenta nuevoDetalle = detalleVentaService.guardar(detalle);
            return new ResponseEntity<>(nuevoDetalle, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error procesando el detalle de venta.");
        }
    }

    @GetMapping("/{codigoDetalle}")
    public ResponseEntity<DetalleVenta> buscarPorId(@PathVariable Integer codigoDetalle) {
        return detalleVentaService.buscarPorId(codigoDetalle)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/venta/{codigoVenta}")
    public ResponseEntity<List<DetalleVenta>> listarPorVenta(@PathVariable Integer codigoVenta) {
        List<DetalleVenta> detalles = detalleVentaService.listarPorVenta(codigoVenta);
        return ResponseEntity.ok(detalles);
    }

    @DeleteMapping("/{codigoDetalle}")
    public ResponseEntity<?> eliminar(@PathVariable Integer codigoDetalle) {
        try {
            if (!detalleVentaService.existePorId(codigoDetalle)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            detalleVentaService.eliminar(codigoDetalle);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("No se pudo eliminar el detalle.");
        }
    }
}