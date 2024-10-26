package ar.edu.unju.fi.poo.tp8poo;

import static org.junit.jupiter.api.Assertions.assertEquals;


import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ar.edu.unju.fi.poo.tp8poo.util.ConversorMoneda;


@SpringBootTest
public class conversorTest {
		
	
	@Test
	public void testConversion() throws IOException {
		double precioActual=150;
		assertEquals(ConversorMoneda.convertirPrecio(precioActual),151125);
	}
	
}
