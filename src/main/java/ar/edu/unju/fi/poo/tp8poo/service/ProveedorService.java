package ar.edu.unju.fi.poo.tp8poo.service;

import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ar.edu.unju.fi.poo.tp8poo.dto.ProveedorDTO;
import ar.edu.unju.fi.poo.tp8poo.mapper.ProveedorMapper;
import ar.edu.unju.fi.poo.tp8poo.repository.ProveedorRepository;
import ar.edu.unju.fi.poo.tp8poo.entity.Proveedor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Servicio para gestionar las operaciones CRUD de los proveedores.
 */
@Slf4j
@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ProveedorMapper proveedorMapper;

    /**
     * Crea un nuevo proveedor a partir de un objeto ProveedorDTO.
     *
     * @param proveedorDTO el objeto DTO con los datos del proveedor a crear
     * @return el proveedor creado como un objeto ProveedorDTO
     */
    public ProveedorDTO crearProveedor(ProveedorDTO proveedorDTO) {
        log.debug("Creando un nuevo proveedor: {}", proveedorDTO);
        proveedorDTO.setEstado(true);
        Proveedor proveedor = proveedorMapper.toProveedor(proveedorDTO);
        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
        log.info("Proveedor creado exitosamente con ID: {}", proveedorGuardado.getId());
        return proveedorMapper.toProveedorDTO(proveedorGuardado);
    }

    /**
     * Obtiene una lista de todos los proveedores.
     *
     * @return una lista de objetos ProveedorDTO representando todos los proveedores
     */
    public List<ProveedorDTO> obtenerProveedores() {
        log.debug("Obteniendo todos los proveedores.");
        List<Proveedor> proveedores = proveedorRepository.findAll();
        log.info("Total de proveedores encontrados: {}", proveedores.size());
        return proveedorMapper.toProveedorDTOList(proveedores);
    }

    /**
     * Obtiene un proveedor por su ID.
     *
     * @param id el ID del proveedor a buscar
     * @return el proveedor encontrado como un objeto ProveedorDTO, o null si no existe
     */
    public ProveedorDTO obtenerProveedorPorId(Long id) {
        log.debug("Buscando proveedor con ID: {}", id);
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Proveedor con ID: {} no encontrado", id);
                    return new NegocioException("Proveedor no encontrado"); // Cambiado para retornar la excepción
                });
        log.info("Proveedor encontrado ID: {}", proveedor.getId());
        return proveedorMapper.toProveedorDTO(proveedor);
    }

    /**
     * Actualiza los datos de un proveedor existente.
     *
     * @param id el ID del proveedor a actualizar
     * @param proveedorDTO el objeto DTO con los nuevos datos del proveedor
     * @return el proveedor actualizado como un objeto ProveedorDTO
     * @throws NegocioException si no se encuentra el proveedor con el ID especificado
     */
    public ProveedorDTO actualizarProveedor(Long id, ProveedorDTO proveedorDTO) {
        log.debug("Actualizando proveedor con ID: {}", id);
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new NegocioException("Proveedor no encontrado con ID: " + id));
        proveedor.setNombre(proveedorDTO.getNombre());
        proveedor.setEmail(proveedorDTO.getEmail());
        proveedor.setTelefono(proveedorDTO.getTelefono());
        Proveedor proveedorActualizado = proveedorRepository.save(proveedor);
        log.info("Proveedor con ID: {} actualizado exitosamente", id);
        return proveedorMapper.toProveedorDTO(proveedorActualizado);
    }

    /**
     * Marca un proveedor como inactivo (estado false) sin eliminarlo de la base de datos.
     *
     * @param id el ID del proveedor a marcar como inactivo
     * @throws NegocioException si no se encuentra el proveedor con el ID especificado
     */
    public void eliminarProveedor(Long id) {
        log.debug("Iniciando la eliminación del proveedor con ID: {}", id);
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new NegocioException("Proveedor no encontrado con ID: " + id));
        proveedor.setEstado(false);  
        proveedorRepository.save(proveedor);
        log.info("Proveedor con ID: {} marcado como inactivo", id);
    }
}