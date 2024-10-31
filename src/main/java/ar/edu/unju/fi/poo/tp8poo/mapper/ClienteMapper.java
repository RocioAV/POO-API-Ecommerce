package ar.edu.unju.fi.poo.tp8poo.mapper;

import ar.edu.unju.fi.poo.tp8poo.dto.ClienteDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClienteEstandarDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Cliente;
import ar.edu.unju.fi.poo.tp8poo.entity.ClienteEstandar;
import ar.edu.unju.fi.poo.tp8poo.entity.ClientePremium;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",uses = {CuponMapper.class})
public interface ClienteMapper {
    // Métodos para objetos individuales
    ClienteEstandarDTO toClienteEstandarDTO(ClienteEstandar clienteEstandar);

    ClientePremiumDTO toClientePremiunDTO(ClientePremium clientePremium);

    ClienteEstandar toClienteEstandarEntity(ClienteEstandarDTO clienteEstandarDTO);

    ClientePremium toClientePremiunEntity(ClientePremiumDTO clientePremiumDTO);

    List<ClienteEstandarDTO> toClienteEstandarDTOList(List<ClienteEstandar> clienteEstandarList);

    List<ClientePremiumDTO> toClientePremiunDTOList(List<ClientePremium> clientesPremium);

    List<ClienteEstandar> toClienteEstandarEntityList(List<ClienteEstandarDTO> clienteEstandarDTOList);

    List<ClientePremium> toClientePremiunEntityList(List<ClientePremiumDTO> clientePremiumDTOList);

    default Cliente toClienteEntity(ClienteDTO clienteDTO) {
        if (clienteDTO instanceof ClienteEstandarDTO) {
            return toClienteEstandarEntity((ClienteEstandarDTO) clienteDTO);
        } else if (clienteDTO instanceof ClientePremiumDTO) {
            return toClientePremiunEntity((ClientePremiumDTO) clienteDTO);
        }
        throw new IllegalArgumentException("Tipo de cliente no soportado: " + clienteDTO.getClass().getName());
    }
    default ClienteDTO toClienteDTO(Cliente cliente) {
        if (cliente instanceof ClienteEstandar) {
            return toClienteEstandarDTO((ClienteEstandar) cliente);
        } else if (cliente instanceof ClientePremium) {
            return toClientePremiunDTO((ClientePremium) cliente);
        }
        return null;
    }

}
