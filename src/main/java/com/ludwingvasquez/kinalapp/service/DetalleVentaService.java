package com.ludwingvasquez.kinalapp.service;

import com.ludwingvasquez.kinalapp.entity.DetalleVenta;
import com.ludwingvasquez.kinalapp.repository.DetalleVentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleVentaService implements IDetalleVentaService {

    private final DetalleVentaRepository detalleVentaRepository;

    public DetalleVentaService(DetalleVentaRepository detalleVentaRepository) {
        this.detalleVentaRepository = detalleVentaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleVenta> listarTodos() {
        return detalleVentaRepository.findAll();
    }

    @Override
    @Transactional
    public DetalleVenta guardar(DetalleVenta detalleVenta) {
        if (detalleVenta.getVenta() == null || detalleVenta.getVenta().getCodigoVenta() == null) {
            throw new RuntimeException("Error: El detalle debe estar asociado a una Venta válida.");
        }
        if (detalleVenta.getProducto() == null || detalleVenta.getProducto().getCodigo_producto() == null) {
            throw new RuntimeException("Error: El detalle debe tener un Producto válido.");
        }

        if (detalleVenta.getCantidad() == null || detalleVenta.getCantidad() <= 0) {
            throw new RuntimeException("Error: La cantidad debe ser mayor a 0.");
        }

        if (detalleVenta.getEstado() == null) {
            detalleVenta.setEstado(1L);
        }

        return detalleVentaRepository.save(detalleVenta);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DetalleVenta> buscarPorId(Integer codigoDetalle) {
        return detalleVentaRepository.findById(codigoDetalle);
    }

    @Override
    @Transactional
    public DetalleVenta actualizar(Integer codigoDetalle, DetalleVenta detalleVenta) {
        if (!existePorId(codigoDetalle)) {
            throw new RuntimeException("No existe el detalle con ID: " + codigoDetalle);
        }

        detalleVenta.setCodigoDetalle(codigoDetalle);

        if (detalleVenta.getVenta() == null || detalleVenta.getProducto() == null) {
            throw new RuntimeException("Datos de Venta o Producto faltantes para actualizar.");
        }

        return detalleVentaRepository.save(detalleVenta);
    }

    @Override
    @Transactional
    public void eliminar(Integer codigoDetalle) {
        if (existePorId(codigoDetalle)) {
            detalleVentaRepository.deleteById(codigoDetalle);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorId(Integer codigoDetalle) {
        return detalleVentaRepository.existsById(codigoDetalle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleVenta> listarPorVenta(Integer codigoVenta) {
        return detalleVentaRepository.findByVenta_CodigoVenta(codigoVenta);
    }
}