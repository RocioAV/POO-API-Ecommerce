package ar.edu.unju.fi.poo.tp8poo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import org.json.JSONObject;


public class ConversorMoneda {

	private  ConversorMoneda(){

	}

	private static String consumoAPI()  {
		int cp;
		HttpURLConnection connection;
		BufferedReader br;
		try {
			URL url = new URL("https://dolarapi.com/v1/dolares/oficial");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");
			if (connection.getResponseCode() != 200) {
				throw new NegocioException("Error con el código HTTP " + connection.getResponseCode());
			}
			br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder str = new StringBuilder();
			while ((cp = br.read()) != -1) {
				str.append((char) cp);
			}
			br.close();
			connection.disconnect();
			return str.toString();
		} catch (IOException e) {
			throw new NegocioException("Error al consumir la API: " + e.getMessage());
		}
	}

	public static double convertirPrecio(double precioActual) {
		double precioVenta;
		String api=consumoAPI();
		JSONObject json= new JSONObject(api);
		precioVenta=json.getDouble("venta")*precioActual;
		return precioVenta;
	}


}
