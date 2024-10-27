package ar.edu.unju.fi.poo.tp8poo.service;

import ar.edu.unju.fi.poo.tp8poo.dto.ClienteDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClienteEstandarDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ProductoDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Cliente;
import ar.edu.unju.fi.poo.tp8poo.exceptions.ClienteNoActivoException;
import ar.edu.unju.fi.poo.tp8poo.exceptions.ProductoNoDIsponibleException;
import ar.edu.unju.fi.poo.tp8poo.mapper.ClienteMapper;
import ar.edu.unju.fi.poo.tp8poo.mapper.VentaMapper;
import ar.edu.unju.fi.poo.tp8poo.repository.VentaRepository;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoCliente;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoProducto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentaService {
    @Autowired
    ClienteService clienteService;

    @Autowired
    ProductoService productoService;

    @Autowired
    VentaRepository ventaRepository;

    @Autowired
    VentaMapper ventaMapper;
    @Autowired
    ClienteMapper clienteMapper;

    private void validarClienteActivo(Long id){
        ClienteDTO cliente = clienteService.buscarPorID(id);
        if (!cliente.getEstado().equals(EstadoCliente.ACTIVO.name())) {
            throw new ClienteNoActivoException("El cliente no esta activo para hacer una compra");
        }
    }

    private void validarProductoDisponible(Long id){
        ProductoDTO producto= productoService.findById(id);
        if(!producto.getEstado().equals(EstadoProducto.DISPONIBLE.getEstado())){
            throw new ProductoNoDIsponibleException("Producto no disponible");
        }
    }

    private Cliente devolverTipoCliente(ClienteDTO clienteDTO) {
        if (clienteDTO instanceof ClientePremiumDTO) {
            return clienteMapper.toClientePremiunEntity((ClientePremiumDTO) clienteDTO);
        } else if (clienteDTO instanceof ClienteEstandarDTO) {
            return clienteMapper.toClienteEstandarEntity((ClienteEstandarDTO) clienteDTO);
        }
        throw new IllegalArgumentException("Tipo de ClienteDTO no soportado: " + clienteDTO.getClass().getName());
    }

}
