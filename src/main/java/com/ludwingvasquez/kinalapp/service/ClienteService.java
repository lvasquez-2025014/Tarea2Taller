package com.ludwingvasquez.kinalapp.service;

import com.ludwingvasquez.kinalapp.entity.Cliente;
import com.ludwingvasquez.kinalapp.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

//Anotacion que registra un Bean como un Bean de Spring
//Que la clase contiene la logica de negocio
@Service
//Por defecto todos los metodos de esta clase seran
//transaccionales
//una transaccion es que puede o no ocurrir algo
@Transactional
public class ClienteService implements IClienteService {
    /* Private: solo es accesible dentro de la clase
        ClienteRepository: es el repositorio para acceder a la base de datos
        Inyeccion de dependencias Spring nos da el repositorio
     */
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
        /*
            El metodo de guardart crea un cliente
            aca es donde colocamos la logica del negocio antes de guardar
            primero validamos el dato
         */
        validarCliente(cliente);
        if(cliente.getEstado()==0){
            cliente.setEstado(1);
        }
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorDPI(String dpi) {
        //buscar un cliente por DPI
        return clienteRepository.findById(dpi);
        //Optional nos evita el NullPointerException
    }

    @Override
    public Cliente actualizar(String dpi, Cliente cliente) {
        //actualiza un cliente existente
        if(!clienteRepository.existsById(dpi)){
            throw new RuntimeException("Cliente no se encontro con DPI "+dpi);
            //si no existe, se lanza una excepcion(error controlado)
        }
        /*
            * 1.Asegurar que el DPI del objeto coincida con el de la URL
            * 2.Por seguridad usamos el DPI de la URL y no el que viene en el JSON
         */
        cliente.setDPICliente(dpi);
        validarCliente(cliente);

        return clienteRepository.save(cliente);
    }

    @Override
    public void eliminar(String dpi) {
        //eliminar un cliente
        if (!clienteRepository.existsById(dpi)){
            throw new RuntimeException("El cliente no se encontro con el DPI "+dpi);
        }
        clienteRepository.deleteById(dpi);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorDPI(String dpi) {
        //verificar si existe el cliente
        return clienteRepository.existsById(dpi);
        //retorna true o false
    }
    // Meotodo privado (Solo pueden utilizarce dentro de la clase)
    private void validarCliente(Cliente cliente){
        /*
         *Validaciones del negocio: Este metodo se hara privado
         * porque es algo interno del servicio
         * */
        if (cliente.getDPICliente() == null || cliente.getDPICliente().trim().isEmpty()){
            //si el dpi en null o esta vacio despues de qui tar espacios
            //Lanza una excepcion con un mensaje
            throw new IllegalArgumentException("El DPI es un dato obligatorio");
        }

        if (cliente.getNombreCliente()==null || cliente.getNombreCliente().trim().isEmpty()){
            throw new IllegalArgumentException("el nombre es un dato obligatorio");
        }

        if(cliente.getApellidoCliente()==null || cliente.getApellidoCliente().trim().isEmpty()){
            throw new IllegalArgumentException("el apellido es un dato obligatorio");
        }
    }
    //metodo para listar activos
    @Transactional(readOnly = true)
    public List<Cliente> listarPorEstado(Integer estado){
        return clienteRepository.findByEstado(estado);
    }

}
