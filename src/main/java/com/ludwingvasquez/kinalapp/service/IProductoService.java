package com.ludwingvasquez.kinalapp.service;

import com.ludwingvasquez.kinalapp.entity.Producto;
import java.util.List;
import java.util.Optional;

public interface IProductoService {
    List<Producto> listarTodos();
    Producto guardar(Producto producto);
    Optional<Producto> buscarPorCodigo(Integer codigo_producto);
    Producto actualizar(Integer codigo_producto, Producto producto);
    void eliminar(Integer codigo_producto);
    boolean existePorCodigo(Integer codigo_producto);
    List<Producto> listarPorEstado(Long estado);
}