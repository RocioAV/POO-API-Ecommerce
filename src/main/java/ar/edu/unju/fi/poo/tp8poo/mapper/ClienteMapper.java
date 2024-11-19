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

    ClienteEstandarDTO toClienteEstandarDTO(ClienteEstandar clienteEstandar);

    ClientePremiumDTO toClientePremiunDTO(ClientePremium clientePremium);

    ClienteEstandar toClienteEstandarEntity(ClienteEstandarDTO clienteEstandarDTO);

    ClientePremium toClientePremiunEntity(ClientePremiumDTO clientePremiumDTO);


    default Cliente toClienteEntity(ClienteDTO clienteDTO) {
        if (clienteDTO instanceof ClienteEstandarDTO clienteEstandarDTO) {
            return toClienteEstandarEntity(clienteEstandarDTO);
        } else if (clienteDTO instanceof ClientePremiumDTO clientePremiumDTO) {
            return toClientePremiunEntity(clientePremiumDTO);
        }
        throw new IllegalArgumentException("Tipo de cliente no soportado: " + clienteDTO.getClass().getName());
    }
    default ClienteDTO toClienteDTO(Cliente cliente) {
        if (cliente instanceof ClienteEstandar clienteEstandar) {
            return toClienteEstandarDTO(clienteEstandar);
        } else if (cliente instanceof ClientePremium clientePremium) {
            return toClientePremiunDTO(clientePremium);
        }
        return null;
    }

    List<ClienteDTO> toClienteDtoList(List<Cliente> clienteList);

}
