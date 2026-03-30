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

        InputStream inputStream = new FileInputStream(rutaArchivo);

        return new MockMultipartFile(
                "file",
                nombreArchivo,
                tipoContenido,
                inputStream);
    }

}
