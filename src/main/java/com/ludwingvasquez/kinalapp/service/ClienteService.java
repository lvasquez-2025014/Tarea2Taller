package com.ludwingvasquez.kinalapp.service;

import com.ludwingvasquez.kinalapp.entity.Cliente;
import com.ludwingvasquez.kinalapp.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService implements IClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        validarCliente(cliente);
        if(cliente.getEstado()==0){
            cliente.setEstado(1);
        }
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorDPI(String dpi) {
        return clienteRepository.findById(dpi);
    }

    @Override
    public Cliente actualizar(String dpi, Cliente cliente) {
        if(!clienteRepository.existsById(dpi)){
            throw new RuntimeException("Cliente no se encontro con DPI "+dpi);
        }
        cliente.setDPICliente(dpi);
        validarCliente(cliente);

        return clienteRepository.save(cliente);
    }

    @Override
    public void eliminar(String dpi) {
        if (!clienteRepository.existsById(dpi)){
            throw new RuntimeException("El cliente no se encontro con el DPI "+dpi);
        }
        clienteRepository.deleteById(dpi);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorDPI(String dpi) {
        return clienteRepository.existsById(dpi);
    }
    private void validarCliente(Cliente cliente){
        if (cliente.getDPICliente() == null || cliente.getDPICliente().trim().isEmpty()){
            throw new IllegalArgumentException("El DPI es un dato obligatorio");
        }

        if (cliente.getNombreCliente()==null || cliente.getNombreCliente().trim().isEmpty()){
            throw new IllegalArgumentException("el nombre es un dato obligatorio");
        }

        if(cliente.getApellidoCliente()==null || cliente.getApellidoCliente().trim().isEmpty()){
            throw new IllegalArgumentException("el apellido es un dato obligatorio");
        }
    }
    @Transactional(readOnly = true)
    public List<Cliente> listarPorEstado(Integer estado){
        return clienteRepository.findByEstado(estado);
    }

}
