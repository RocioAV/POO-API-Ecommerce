package ar.edu.unju.fi.poo.tp8poo;

import ar.edu.unju.fi.poo.tp8poo.dto.CuponDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Cupon;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.repository.CuponRepository;
import ar.edu.unju.fi.poo.tp8poo.service.CuponService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

@Transactional
@SpringBootTest
class TestCuponService {
    @Autowired
    private CuponService cuponService;

    @Autowired
    private CuponRepository cuponRepository;

    private CuponDTO cuponDTO;

    @BeforeEach
    void setUp() {
        cuponDTO = new CuponDTO();
        cuponDTO.setPorcentajeDescuento(15.0);
        cuponDTO.setFechaExpiracion(LocalDate.now().plusDays(30).toString());
    }

    @Test
    void testCrearCupon() {
        Cupon cuponCreado = cuponService.crearCupon(cuponDTO);
        assertNotNull(cuponCreado.getId(), "El cupón debería tener un ID asignado.");
        assertEquals(15.0, cuponCreado.getPorcentajeDescuento(), "El porcentaje de descuento debe ser 15.");
    }

    @Test
    void testCrearCuponFechaExpiracionInvalida() {
        cuponDTO.setFechaExpiracion(LocalDate.now().minusDays(1).toString());

        NegocioException exception = assertThrowsExactly(NegocioException.class, () -> {
            cuponService.validarNuevoCupon(LocalDate.parse(cuponDTO.getFechaExpiracion()), cuponDTO.getPorcentajeDescuento());
        });

        assertEquals("La fecha de expiración del cupón debe ser posterior a la fecha actual.", exception.getMessage());
    }
    @Test
    void testValidarNuevoCupon_DescuentoFueraDeRango() {
        NegocioException exception = assertThrowsExactly(NegocioException.class, () -> {
            cuponService.validarNuevoCupon(LocalDate.parse(cuponDTO.getFechaExpiracion()), 150.0);
        });

        assertEquals("El porcentaje de descuento debe estar entre 0 y 100.", exception.getMessage());
    }

}
