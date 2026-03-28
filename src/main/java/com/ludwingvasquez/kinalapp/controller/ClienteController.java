package com.ludwingvasquez.kinalapp.controller;

import com.ludwingvasquez.kinalapp.entity.Cliente;
import com.ludwingvasquez.kinalapp.service.IClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
//@RestController = @Controller + @ResponseBody
@RequestMapping("/clientes")
//Todas las rutas en este controlador deben empezar con /clientes
public class ClienteController {

    //inyectamos el SERVICIO y NO el repositorio
    //el controlador solo debe de tenrer conexion con el Servicio
    private final IClienteService clienteService;

    //como buena practiva la inyeccion de dependencias deber hacerce por el constructor
    public ClienteController(IClienteService clienteService) {
        this.clienteService = clienteService;
    }

    //Responde a peticiones GET
    @GetMapping
    //ResponseEntity nos permite controlar el codigo HTTP y el cuerpo
    public ResponseEntity<List<Cliente>> listar(){
        List<Cliente> clientes = clienteService.listarTodos();
        //delegamos al servicio
        return ResponseEntity.ok(clientes);
        //200 ok con la lista de clientes
    }

    // {dpi} es una variable de ruta (valor a buscar)
    @GetMapping("/{dpi}")
    public ResponseEntity<Cliente> buscarPorDPI(@PathVariable String dpi){
        // @PathVariable toma el valor de la URL y lo asigna a dpi
        return clienteService.buscarPorDPI(dpi)
                // Si Optional tiene valor, devuelve 200 OK con el cliente
                .map(ResponseEntity::ok)
                // Si Optional está vacío, devuelve 404 NOT FOUND
                .orElse(ResponseEntity.notFound().build());
    }
    // POST crear un nuevo cliente
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Cliente cliente){
        // @RequestBody toma el JSON del cuerpo y lo convierte a un objeto Cliente
        // <?> significa "tipo genérico" puede ser un Cliente o un String
        try {
            Cliente nuevoCliente = clienteService.guardar(cliente);
            //Intentamos guardar el cliente pero puede lanzar una excepcion
            // de IllegalArgumentException
            return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
            //201 CREATED(mucho mas especifico que el 2200 para la creacion de un cliente)
        } catch (IllegalArgumentException e){
            // Si hay error de validación
            return ResponseEntity.badRequest().body(e.getMessage());
            //400 BAD REQUEST con el mensaje de error
        }
    }

    //DELETE elimina un cliente
    @DeleteMapping("/{dpi}")
    public ResponseEntity<Void> eliminar(@PathVariable String dpi){
        //responseEntity<void> no devuelve nada
        try{
            if(!clienteService.existePorDPI(dpi)){
                return ResponseEntity.notFound().build();
                //404 si=\
                // 3
                // no existe
            }
            clienteService.eliminar(dpi);
            return ResponseEntity.noContent().build();
            //204 no content(se ejecuto correctamente y no devuelve cuerpo)
        }
        catch (RuntimeException e){
            return  ResponseEntity.notFound().build();
        }
    }
    //Actualizar cliente a traves de DPI
    @PutMapping("/{dpi}")
    public ResponseEntity<?> actualizar(@PathVariable String dpi, @RequestBody Cliente cliente){
        try{
            if(!clienteService.existePorDPI(dpi)){
                //verificar si existe antes de poder actualizar
                //si no existe 404 NOT FOUND
                return ResponseEntity.notFound().build();
            }
            //actualizar el cliente pero esto puede lanzar una excepcion
            Cliente clienteActualizado = clienteService.actualizar(dpi,cliente);
            return ResponseEntity.ok(clienteActualizado);
            //200 ok con el cliente ya actualizado
        }catch (IllegalArgumentException e){
            //Error cuando los datos son incorrectos
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (RuntimeException e){
            //posiblemente cualquier otro como por ejemplo cliente no encontrado, etc.
            //404 NOT FUOUND
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping( "/activos" )
    public ResponseEntity<List<Cliente>> listarActivos(){
        List<Cliente> activos = clienteService.listarPorEstado(1);

        return ResponseEntity.ok(activos);
    }

    @GetMapping("/inactivos")
    public ResponseEntity<List<Cliente>>listarInactivos(){
        List<Cliente> inactivos = clienteService.listarPorEstado(0);

        return ResponseEntity.ok(inactivos);
    }
}