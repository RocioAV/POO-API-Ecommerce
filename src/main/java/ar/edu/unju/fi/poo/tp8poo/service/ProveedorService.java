package ar.edu.unju.fi.poo.tp8poo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.unju.fi.poo.tp8poo.dto.ProveedorDTO;
import ar.edu.unju.fi.poo.tp8poo.mapper.ProveedorMapper;
import ar.edu.unju.fi.poo.tp8poo.repository.ProveedorRepository;
import ar.edu.unju.fi.poo.tp8poo.entity.Proveedor;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ProveedorMapper proveedorMapper;

    public ProveedorDTO crearProveedor(ProveedorDTO proveedorDTO) {
        Proveedor proveedor = proveedorMapper.toProveedor(proveedorDTO);
        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
        return proveedorMapper.toProveedorDTO(proveedorGuardado);
    }

    public List<ProveedorDTO> obtenerProveedores() {
        List<Proveedor> proveedores = proveedorRepository.findAll();
        return proveedorMapper.toProveedorDTOList(proveedores);
    }

    public ProveedorDTO obtenerProveedorPorId(Long id) {
        Optional<Proveedor> proveedor = proveedorRepository.findById(id);
        return proveedor.map(proveedorMapper::toProveedorDTO).orElse(null);
    }

    public ProveedorDTO actualizarProveedor(Long id, ProveedorDTO proveedorDTO) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));

        proveedor.setNombre(proveedorDTO.getNombre());
        proveedor.setEmail(proveedorDTO.getEmail());
        proveedor.setTelefono(proveedorDTO.getTelefono());

        Proveedor proveedorActualizado = proveedorRepository.save(proveedor);
        ProveedorDTO proveedorActualizadoDTO=proveedorMapper.toProveedorDTO(proveedorActualizado);
        return proveedorActualizadoDTO;
    }

    public void eliminarProveedor(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));
        proveedor.setEstado(false);  
        proveedorRepository.save(proveedor); 
    }
}
