package com.ludwingvasquez.kinalapp.service;

import com.ludwingvasquez.kinalapp.entity.Producto;
import com.ludwingvasquez.kinalapp.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService implements IProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Override
    @Transactional
    public Producto guardar(Producto producto) {
        validarProducto(producto);
        if (producto.getEstado() == 0) {
            producto.setEstado(1L);
        }
        return productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorCodigo(Integer codigo_producto) {
        return productoRepository.findById(codigo_producto);
    }

    @Override
    @Transactional
    public Producto actualizar(Integer codigo_producto, Producto producto) {
        if (!productoRepository.existsById(codigo_producto)) {
            throw new RuntimeException("El producto no se encontró con el código: " + codigo_producto);
        }
        producto.setCodigo_producto(codigo_producto);
        validarProducto(producto);
        return productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void eliminar(Integer codigo_producto) {
        if (!productoRepository.existsById(codigo_producto)) {
            throw new RuntimeException("El producto no se encontró con el código: " + codigo_producto);
        }
        productoRepository.deleteById(codigo_producto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorCodigo(Integer codigo_producto) {
        return productoRepository.existsById(codigo_producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarPorEstado(Long estado) {
        return productoRepository.findByEstado(estado);
    }

    private void validarProducto(Producto producto) {
        if (producto.getNombre_producto() == null || producto.getNombre_producto().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }

        if (producto.getPrecio() == null || producto.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio debe ser un valor válido y no negativo");
        }

        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }
}