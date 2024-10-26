package ar.edu.unju.fi.poo.tp8poo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;


public class ConversorMoneda {
	
	@Autowired
	private static String consumoAPI() throws IOException {
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
			return str.toString();
	}
	
	public static double convertirPrecio(double precioActual) throws IOException {
		double precioVenta;
		String api=consumoAPI();
		JSONObject json= new JSONObject(api);
		precioVenta=json.getDouble("venta")*precioActual;
		return precioVenta;
	}
	
	
}
