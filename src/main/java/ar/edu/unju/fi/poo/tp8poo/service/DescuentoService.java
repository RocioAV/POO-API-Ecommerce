package ar.edu.unju.fi.poo.tp8poo.service;

import ar.edu.unju.fi.poo.tp8poo.dto.ClienteDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClienteEstandarDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class DescuentoService {
    /**
     * Calcula el descuento en base a un porcentaje y precio dado.
     *
     * @param porcentaje  Porcentaje de descuento.
     * @param precioProd  Precio original del producto.
     * @return Monto del descuento aplicado.
     */
    private Double calcularDescuento(Double porcentaje, Double precioProd){
        log.debug("Calculando descuento del {}% sobre el precio {}", porcentaje,precioProd);
        return porcentaje * precioProd / 100;
    }

    /**
     * Verifica y aplica el descuento específico para un cliente premium.
     *
     * @param clientePremiumDTO Cliente premium a verificar.
     * @param precioProducto Precio original del producto.
     * @return Precio final después de aplicar el descuento, si corresponde.
     */
    private Double verificarDescuentoClientePremiun(ClientePremiumDTO clientePremiumDTO, Double precioProducto){
        log.info("Verificando descuento para cliente premium con ID {}", clientePremiumDTO.getId());
        if(clientePremiumDTO.getPorcentajeDescuento()!=null){
            if (clientePremiumDTO.getPorcentajeDescuento()<= 0.0 || clientePremiumDTO.getPorcentajeDescuento()>100 ){
                log.warn("Porcentaje de descuento inválido para cliente premium");
                return precioProducto;
            }else{
                Double descuento=calcularDescuento(clientePremiumDTO.getPorcentajeDescuento(),precioProducto);
                log.debug("Descuento  aplicado: {}, Precio final: {}", descuento, precioProducto - descuento);
                return precioProducto-descuento;
            }
        }else{
            return precioProducto;
        }

    }
    /**
     * Verifica si la fecha de un cupón ha expirado.
     *
     * @param fechaExpiracion Fecha de expiración del cupón.
     * @return true si el cupón ha expirado, false en caso contrario.
     */
    public boolean isExpirado(LocalDate fechaExpiracion) {
        log.debug("Verificando si el cupón ha expirado para la fecha: {}", fechaExpiracion);
        LocalDate ahora = LocalDate.now();
        return fechaExpiracion.isBefore(ahora);
    }
    /**
     * Verifica y aplica el descuento específico para un cliente estándar.
     *
     * @param clienteEstandarDTO Cliente estándar a verificar.
     * @param precioProducto Precio original del producto.
     * @return Precio final después de aplicar el descuento, si corresponde.
     */
    private Double verificarDescuentoClienteEstandar(ClienteEstandarDTO clienteEstandarDTO, Double precioProducto){
        log.info("Verificando descuento para cliente estándar con ID {}", clienteEstandarDTO.getId());
        if (clienteEstandarDTO.getCupon()!=null){
            if(!isExpirado(LocalDate.parse(clienteEstandarDTO.getCupon().getFechaExpiracion()))){
                Double descuento= calcularDescuento(clienteEstandarDTO.getCupon().getPorcentajeDescuento(),precioProducto);
                log.debug("Descuento aplicado: {}, Precio final: {}", descuento, precioProducto - descuento);
                return precioProducto-descuento;
            }else {
                log.warn("Cupón expirado para cliente estándar con ID {}", clienteEstandarDTO.getId());
                return precioProducto;
            }
        }else {
            log.warn("No existe cupon para cliente estándar con ID {}", clienteEstandarDTO.getId());
            return precioProducto;
        }
    }
    /**
     * Aplica el descuento adecuado al precio del producto según el tipo de cliente.
     *
     * @param precioProducto Precio original del producto.
     * @param clienteDTO Cliente al que se le aplicará el descuento.
     * @return Precio final después de aplicar el descuento.
     */
    public Double aplicarDescuento(Double precioProducto, ClienteDTO clienteDTO) {
        log.info("Aplicando descuento para cliente con ID {}", clienteDTO.getId());
        Double precioFinal = clienteDTO instanceof ClientePremiumDTO
                ? verificarDescuentoClientePremiun((ClientePremiumDTO) clienteDTO, precioProducto)
                : verificarDescuentoClienteEstandar((ClienteEstandarDTO) clienteDTO, precioProducto);
        log.debug("Precio final después del descuento: {}", precioFinal);
        return precioFinal;
    }
}
