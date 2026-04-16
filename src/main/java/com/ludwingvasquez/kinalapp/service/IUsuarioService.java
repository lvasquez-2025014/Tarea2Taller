package com.ludwingvasquez.kinalapp.service;

import com.ludwingvasquez.kinalapp.entity.Usuario;
import java.util.List;
import java.util.Optional;

public interface IUsuarioService {

    List<Usuario> listarTodos();

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long codigoUsuario);

    Usuario actualizar(Long codigoUsuario, Usuario usuario);

    void eliminar(Long codigoUsuario);

    boolean existePorCodigo(Long codigoUsuario);

    Optional<Usuario> buscarPorUsername(String username);
}