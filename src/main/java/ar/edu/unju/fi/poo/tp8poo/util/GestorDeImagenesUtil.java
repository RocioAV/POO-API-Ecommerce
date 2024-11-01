package ar.edu.unju.fi.poo.tp8poo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Clase que provee metodos para la gestion de subida, actualizacion y eliminacion de las imagenes
 * utilizando el servicio de almacenamiento en la nube de Firebase
 * 
 */
@Component
public class GestorDeImagenesUtil {

	@Autowired
	private FirebaseStorageUtil firebaseStorageUtil;
	
	/**
	 * Subir imagen y obtener su URL.
	 * 
	 * @param imagen a subir
	 * @param nombreCarpeta donde se guardara la imagen
	 * @return URL de la imagen subida
	 * @throws RuntimeException si hay fallo con la subida de imagen
	 */
	public String subirImagen(MultipartFile imagen, String nombreCarpeta) {
		if (imagen == null || imagen.isEmpty()) {
			throw new RuntimeException("El archivo MultipartFile no tiene contenido o es nulo");
		}
		return firebaseStorageUtil.subirArchivo(imagen, nombreCarpeta);
	}

	/**
	 * Actualiza la imagen y eliminacion de la anterior.
	 * En caso de no porporcionar una imagen sin contenido se evalua si
	 * se desea borrar la imagen actual o mantener la misma
	 * 
	 * @param imagen a subir
	 * @param URLAnterior URL de la anterior imagen
	 * @param folderName carpeta donde se guardara
	 * @param eliminarFoto que indica la eliminacion de la foto o no
	 * @return URL de la imagen subida o de la anterior
	 * @throws RuntimeException si hay fallo con la subida o de imagen
	 */
	public String actualizarImagen(MultipartFile imagen, String URLAnterior, String folderName, 
			boolean eliminarFoto) {
		String URL = null;
		if (imagen != null && !imagen.isEmpty()) {
			// se ha subido una nueva imagen
			URL = this.subirImagen(imagen, folderName);
			if (URLAnterior != null) {
				firebaseStorageUtil.eliminarArchivo(URLAnterior, folderName);
			}
		} else {
			// no se ha subido una imagen (posible eliminación o no se ha cambiado la imagen)
			if (eliminarFoto) {
				firebaseStorageUtil.eliminarArchivo(URLAnterior, folderName);
			} else {
				URL = URLAnterior;
			}
		}
		return URL;
	}

}
