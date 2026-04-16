package com.ludwingvasquez.kinalapp.service;

import com.ludwingvasquez.kinalapp.entity.Venta;
import com.ludwingvasquez.kinalapp.entity.DetalleVenta;
import com.ludwingvasquez.kinalapp.repository.VentasRepository;
import com.ludwingvasquez.kinalapp.repository.ClienteRepository;
import com.ludwingvasquez.kinalapp.repository.UsuarioRepository;
import com.ludwingvasquez.kinalapp.repository.DetalleVentaRepository;
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
    private final DetalleVentaRepository detalleVentaRepository;

    public VentaService(VentasRepository ventasRepository,
                        ClienteRepository clienteRepository,
                        UsuarioRepository usuarioRepository,
                        DetalleVentaRepository detalleVentaRepository) {
        this.ventasRepository = ventasRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.detalleVentaRepository = detalleVentaRepository;
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



    @Override
    @Transactional(readOnly = true)
    public long contarVentasPorCliente(String dpiCliente) {
        return ventasRepository.findAll().stream()
            .filter(v -> v.getCliente() != null 
                && dpiCliente.equals(v.getCliente().getDPICliente())
                && v.getEstado() != null && v.getEstado() == 1)
            .count();
    }

    @Override
    @Transactional(readOnly = true)
    public double calcularTotalVentasPorCliente(String dpiCliente) {
        return ventasRepository.findAll().stream()
            .filter(v -> v.getCliente() != null 
                && dpiCliente.equals(v.getCliente().getDPICliente())
                && v.getEstado() != null && v.getEstado() == 1)
            .flatMap(v -> v.getDetalles() != null ? v.getDetalles().stream() : java.util.stream.Stream.empty())
            .filter(d -> d.getEstado() != null && d.getEstado() == 1)
            .mapToDouble(d -> (d.getCantidad() != null ? d.getCantidad() : 0) 
                          * (d.getPrecioUnitario() != null ? d.getPrecioUnitario() : 0.0))
            .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public int contarTotalVentas() {
        return (int) ventasRepository.findAll().stream()
            .filter(v -> v.getEstado() != null && v.getEstado() == 1)
            .count();
    }

    @Override
    @Transactional(readOnly = true)
    public double calcularIngresosTotales() {
        return detalleVentaRepository.findAll().stream()
            .filter(d -> d.getEstado() != null && d.getEstado() == 1)
            .mapToDouble(d -> (d.getCantidad() != null ? d.getCantidad() : 0) 
                          * (d.getPrecioUnitario() != null ? d.getPrecioUnitario() : 0.0))
            .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarVentasPorCliente(String dpiCliente) {
        return ventasRepository.findAll().stream()
            .filter(v -> v.getCliente() != null 
                && dpiCliente.equals(v.getCliente().getDPICliente())
                && v.getEstado() != null && v.getEstado() == 1)
            .toList();
    }
}