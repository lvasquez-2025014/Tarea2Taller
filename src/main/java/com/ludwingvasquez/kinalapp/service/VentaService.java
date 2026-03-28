package com.ludwingvasquez.kinalapp.service;

import com.ludwingvasquez.kinalapp.entity.Venta;
import com.ludwingvasquez.kinalapp.repository.VentasRepository;
import com.ludwingvasquez.kinalapp.repository.ClienteRepository;
import com.ludwingvasquez.kinalapp.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService implements IVentaService {

    private final VentasRepository ventasRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public VentaService(VentasRepository ventasRepository,
                        ClienteRepository clienteRepository,
                        UsuarioRepository usuarioRepository) {
        this.ventasRepository = ventasRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarTodos() {
        return ventasRepository.findAll();
    }

    @Override
    @Transactional
    public Venta guardar(Venta venta) {
        if (venta.getCliente() == null || !clienteRepository.existsById(venta.getCliente().getDPICliente())) {
            throw new RuntimeException("Error: El Cliente proporcionado no existe en el sistema.");
        }

        if (venta.getUsuario() == null || !usuarioRepository.existsById(venta.getUsuario().getCodigoUsuario())) {
            throw new RuntimeException("Error: El Usuario proporcionado no existe en el sistema.");
        }

        if (venta.getEstado() == null) {
            venta.setEstado(1L);
        }
        if (venta.getFechaVenta() == null) {
            venta.setFechaVenta(LocalDate.now());
        }

        return ventasRepository.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Venta> buscarCV(Long codigoVenta) {
        return ventasRepository.findById(codigoVenta);
    }

    @Override
    @Transactional
    public Venta actualizar(Long codigoVenta, Venta venta) {
        if (!existePorCV(codigoVenta)) {
            throw new RuntimeException("No existe la venta con ID: " + codigoVenta);
        }

        if (venta.getCliente() == null || !clienteRepository.existsById(venta.getCliente().getDPICliente()) ||
                venta.getUsuario() == null || !usuarioRepository.existsById(venta.getUsuario().getCodigoUsuario())) {
            throw new RuntimeException("Datos de Cliente o Usuario inválidos o inexistentes.");
        }

        venta.setCodigoVenta(codigoVenta);
        return ventasRepository.save(venta);
    }

    @Override
    @Transactional
    public void eliminar(Long codigoVenta) {
        if (existePorCV(codigoVenta)) {
            ventasRepository.deleteById(codigoVenta);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorCV(Long codigoVenta) {
        return ventasRepository.existsById(codigoVenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarActivas() {
        return ventasRepository.findByEstado(1L);
    }
}