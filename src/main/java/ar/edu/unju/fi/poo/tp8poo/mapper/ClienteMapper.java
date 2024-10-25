package ar.edu.unju.fi.poo.tp8poo.mapper;

import ar.edu.unju.fi.poo.tp8poo.dto.ClienteEstandarDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.ClienteEstandar;
import ar.edu.unju.fi.poo.tp8poo.entity.ClientePremium;
import ar.edu.unju.fi.poo.tp8poo.entity.Cupon;
import ar.edu.unju.fi.poo.tp8poo.exceptions.PorcentajeDescuentoException;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class ClienteMapper {

    @Autowired
    CuponMapper cuponMapper;

    /*SECCION DE CONVERSIONES*/
    /**
     * Conversión de objeto a Objeto DTO
     *
     * @param clienteEstandar se recibe a la Entidad original
     * @return la entidad como un objeto DTO.
     */

    public ClienteEstandarDTO toEstandarDTO(ClienteEstandar clienteEstandar) {

        ClienteEstandarDTO clienteEstandarDTO = new ClienteEstandarDTO();
        clienteEstandarDTO.setNombre(clienteEstandar.getNombre());
        clienteEstandarDTO.setApellido(clienteEstandar.getApellido());
        clienteEstandarDTO.setCreated(clienteEstandar.getCreated());
        clienteEstandarDTO.setCelular(clienteEstandar.getCelular());
        clienteEstandarDTO.setFoto(clienteEstandar.getFoto());
        clienteEstandarDTO.setEmail(clienteEstandar.getEmail());
        if (clienteEstandarDTO.getCupon() != null) {
            if(clienteEstandar.getCupon().getPorcentajeDescuento()>99 || clienteEstandar.getCupon().getPorcentajeDescuento()<0) {
                throw new PorcentajeDescuentoException("Porcentaje de descuento invalido");
            }
            Cupon cupon = new Cupon();
            cupon.setId(clienteEstandarDTO.getCupon().getId());
            cupon.setPorcentajeDescuento(clienteEstandarDTO.getCupon().getPorcentajeDescuento());
            cupon.setFechaExpiracion(clienteEstandarDTO.getCupon().getFechaExpiracion());
            clienteEstandar.setCupon(cupon);
        }
        if(clienteEstandar.getUpdated()==null) {
            clienteEstandarDTO.setUpdated(null);
        }else {
            clienteEstandarDTO.setUpdated(clienteEstandar.getUpdated());
        }
        clienteEstandarDTO.setId(clienteEstandar.getId());
        clienteEstandarDTO.setCupon(cuponMapper.toCuponDTO(clienteEstandar.getCupon()));
        clienteEstandarDTO.setEstado(clienteEstandar.getEstado().name());//Para convertir de un enum a String
        return clienteEstandarDTO;
    }


    /**
     * Conversión de DTO a Objeto en este caso de cliente Estandar
     *
     * @param clienteEstandarDTO se recibe el objeto DTO
     * @return la entidad como una Entidad.
     *
     */
    public ClienteEstandar toEstandarEntity(ClienteEstandarDTO clienteEstandarDTO) {
        ClienteEstandar clienteEstandar = new ClienteEstandar();

        clienteEstandar.setId(clienteEstandarDTO.getId()); // Opcional si es un nuevo cliente
        clienteEstandar.setNombre(clienteEstandarDTO.getNombre());
        clienteEstandar.setApellido(clienteEstandarDTO.getApellido());
        clienteEstandar.setCelular(clienteEstandarDTO.getCelular());
        clienteEstandar.setFoto(clienteEstandarDTO.getFoto());
        clienteEstandar.setEmail(clienteEstandarDTO.getEmail());

        // Convertir String de estado a Enum
        clienteEstandar.setEstado(EstadoCliente.valueOf(clienteEstandarDTO.getEstado()));

        // Manejar el cupón (evitar NPE)
        if (clienteEstandarDTO.getCupon() != null) {
            Cupon cupon = new Cupon();
            cupon.setId(clienteEstandarDTO.getCupon().getId());
            cupon.setPorcentajeDescuento(clienteEstandarDTO.getCupon().getPorcentajeDescuento());
            cupon.setFechaExpiracion(clienteEstandarDTO.getCupon().getFechaExpiracion());
            clienteEstandar.setCupon(cupon);
        }


        return clienteEstandar;
    }

    /*Convertir de Entity a DTO (Premium)*/
    public ClientePremiumDTO toPremiumDTO(ClientePremium clientePremium) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        ClientePremiumDTO dtoPremium = new ClientePremiumDTO();
        dtoPremium.setId(clientePremium.getId());
        dtoPremium.setNombre(clientePremium.getNombre());
        dtoPremium.setApellido(clientePremium.getApellido());
        dtoPremium.setEmail(clientePremium.getEmail());
        dtoPremium.setCelular(clientePremium.getCelular());
        dtoPremium.setFoto(clientePremium.getFoto());
        dtoPremium.setPorcentajeDescuento(clientePremium.getPorcentajeDescuento());
        dtoPremium.setCreated(clientePremium.getCreated());
        dtoPremium.setEstado(clientePremium.getEstado().name());
        return dtoPremium;
    }

    /*Convertir de DTO a Entity(Premium)*/

    public ClientePremium toPremiumEntityDTO(ClientePremiumDTO dto) {
        ClientePremium clientePremium = new ClientePremium();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        clientePremium.setId(dto.getId());
        clientePremium.setNombre(dto.getNombre());
        clientePremium.setApellido(dto.getApellido());
        clientePremium.setEmail(dto.getEmail());
        clientePremium.setCelular(dto.getCelular());
        clientePremium.setFoto(dto.getFoto());
        clientePremium.setEstado(EstadoCliente.valueOf(dto.getEstado()));
        clientePremium.setPorcentajeDescuento(dto.getPorcentajeDescuento());
        clientePremium.setCreated(dto.getCreated());
        return clientePremium;
    }




    /*FIN DE SECCION DE CONVERSION*/
}
