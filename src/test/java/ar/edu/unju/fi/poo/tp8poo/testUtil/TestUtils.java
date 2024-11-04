package ar.edu.unju.fi.poo.tp8poo.testUtil;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestUtils {
    public static MultipartFile generarMultipartFile(String rutaArchivo) throws IOException {
        String nombreArchivo = rutaArchivo.substring(rutaArchivo.lastIndexOf("/") + 1);
        String tipoContenido = "image/" + rutaArchivo.substring(rutaArchivo.lastIndexOf(".") + 1);

        // Lee una imagen real del sistema de archivos
        InputStream inputStream = new FileInputStream(rutaArchivo);

        // Crear un MockMultipartFile con una imagen real
        MultipartFile multipartFile = new MockMultipartFile(
                "file", // Nombre del parámetro
                nombreArchivo, // Nombre del archivo
                tipoContenido, // Tipo de contenido (MIME)
                inputStream); // Contenido del archivo

        return multipartFile;
    }

}
