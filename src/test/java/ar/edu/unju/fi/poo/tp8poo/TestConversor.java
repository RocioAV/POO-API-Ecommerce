package ar.edu.unju.fi.poo.tp8poo;


import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ar.edu.unju.fi.poo.tp8poo.util.ConversorMoneda;


@SpringBootTest
class TestConversor {

	@Test
	void testConversion() {
		double precioActual=150;
		assertTrue(ConversorMoneda.convertirPrecio(precioActual)>5000);
	}
	
}
