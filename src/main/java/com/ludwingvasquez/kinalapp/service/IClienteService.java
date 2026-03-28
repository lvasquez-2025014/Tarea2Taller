package com.ludwingvasquez.kinalapp.service;

import com.ludwingvasquez.kinalapp.entity.Cliente;
import java.util.List;
import java.util.Optional;

public interface IClienteService {

    List<Cliente> listarTodos();

    Cliente guardar(Cliente cliente);

    Optional<Cliente> buscarPorDPI(String dpi);

    Cliente actualizar(String dpi, Cliente cliente);

    void eliminar(String dpi);

    boolean existePorDPI(String dpi);

    List<Cliente> listarPorEstado(Integer estado);
}