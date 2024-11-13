package ar.edu.unju.fi.poo.tp8poo.service;

import ar.edu.unju.fi.poo.tp8poo.dto.CuponDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.repository.CuponRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ar.edu.unju.fi.poo.tp8poo.entity.Cupon;

import java.time.LocalDate;

@Service
@Slf4j
public class CuponService {
    private final CuponRepository cuponRepository;

    public CuponService(CuponRepository cuponRepository){
        this.cuponRepository=cuponRepository;
    }


    public boolean isExpirado(LocalDate fechaExpiracion) {
        log.debug("Verificando si el cupón ha expirado para la fecha: {}", fechaExpiracion);
        LocalDate ahora = LocalDate.now();
        return fechaExpiracion.isBefore(ahora);
    }

    /**
     * Actualiza la fecha de expiración de un cupón existente.
     * @param cuponDTO el cupón a actualizar
     */
    public void actualizarFechaExpiracion(CuponDTO cuponDTO) {
        Cupon cupon = cuponRepository.findById(cuponDTO.getId())
                .orElseThrow(() -> new NegocioException("Cupón no encontrado con id: " + cuponDTO.getId()));
        cupon.setFechaExpiracion(LocalDate.parse(cuponDTO.getFechaExpiracion()));
        cuponRepository.save(cupon);
    }
}
