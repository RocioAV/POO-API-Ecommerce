package ar.edu.unju.fi.poo.tp8poo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ar.edu.unju.fi.poo.tp8poo.util.ConversorMoneda;


@SpringBootTest
public class conversorTest {
	
	
	@Test
	@Disabled
	public void testAPI() throws IOException {
		int cp;
		URL url= new URL("https://dolarapi.com/v1/dolares/oficial");
		HttpURLConnection connection=(HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");
		if(connection.getResponseCode()!=200) {
			throw new RuntimeException("Error con el código HTTP "+connection.getResponseCode());
		}
		BufferedReader br= new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder str=new StringBuilder();
		while((cp=br.read())!=-1) {
			str.append((char)cp);
		}
	}
	
	@Test
	public void testConversion() throws IOException {
		double precioActual=150;
		ConversorMoneda conversor=new ConversorMoneda();
		assertEquals(conversor.convertirPrecio(precioActual), 150825);
	}
	
}
