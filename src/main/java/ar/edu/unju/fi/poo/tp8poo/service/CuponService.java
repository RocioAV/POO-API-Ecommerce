package ar.edu.unju.fi.poo.tp8poo.service;

import ar.edu.unju.fi.poo.tp8poo.dto.CuponDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Cliente;
import ar.edu.unju.fi.poo.tp8poo.entity.ClienteEstandar;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.mapper.CuponMapper;
import ar.edu.unju.fi.poo.tp8poo.repository.CuponRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ar.edu.unju.fi.poo.tp8poo.entity.Cupon;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
@Slf4j
public class CuponService {
    private final CuponRepository cuponRepository;
    private final CuponMapper cuponMapper;

    public CuponService(CuponRepository cuponRepository,CuponMapper cuponMapper) {
        this.cuponRepository=cuponRepository;
        this.cuponMapper=cuponMapper;
    }

    /**
     * Una vez utilizado el cupon por el cliente, cambia la fecha de expiracion para volverlo invalido, asi no volver a aplicar
     * el descuento hasta su renovacion
     * @param cliente cliente con el cupon utilizado
     */
    public void expirarCuponPorUso(Cliente cliente){
        if(cliente instanceof ClienteEstandar clienteEstandar && !clienteEstandar.cuponVencido()){
            clienteEstandar.expirarCupon();
        }

    }

    /**
     * Crea el cupon para persistirlo en la Base de Datos
     * @param cuponDTO nuevo cupon a crear en la BD
     * @return Retorna el Cupon creado, pero como Entidad
     */
    public Cupon crearCupon(CuponDTO cuponDTO) {
        cuponDTO.setId(null);
        Cupon cupon = cuponMapper.toCuponEntity(cuponDTO);
        return cuponRepository.save(cupon);
    }

    /**
     * Valida los nuevos datos de Cupon, para su actualizacion o asignacion
     * @param fechaExpiracionNueva, la fecha de expiracion nueva debe ser posterior a hoy
     * @param porcentajeDescuento el porcentaje de descuento debe ser entre 0 y 100
     * @throws NegocioException lanza la exception si incumple con los requisitos o el formato de la fecha es invalido
     */
    public void validarNuevoCupon(LocalDate fechaExpiracionNueva,Double porcentajeDescuento){
            if (!fechaExpiracionNueva.isAfter(LocalDate.now())) {
                log.warn("La fecha de expiración del nuevo cupón debe ser posterior a la fecha actual.");
                throw new NegocioException("La fecha de expiración del cupón debe ser posterior a la fecha actual.");
            }
            if (porcentajeDescuento <= 0 || porcentajeDescuento >= 100) {
                log.warn("El porcentaje de descuento debe estar entre 0 y 100.");
                throw new NegocioException("El porcentaje de descuento debe estar entre 0 y 100.");
            }

    }
}
