package ar.edu.unju.fi.poo.tp8poo.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FirebaseStorageUtil {

    private String BUCKET_NAME = "tp8poo2024.firebasestorage.app";

    private final Storage storage;

    // Injección de dependencia
    public FirebaseStorageUtil(FirebaseApp firebaseApp) {
        this.storage = StorageClient.getInstance(firebaseApp).bucket().getStorage();
    }
    
    /**
     * Subida de archivos a la nube de Firebase.
     * Se genera un nombre para el archivo, se crea un BlobInfo
     * que se subirá y creará dentro del bucket en la nube, y se 
     * devuelve la URL de descarga autenticada.
     * 
     * @param file archivo a subir
     * @param folder nombre de la carpeta en la que se guardará
     * @throws RuntimeException si hay fallo con la subida del archivo
     */
    public String subirArchivo(MultipartFile file, String folder) {
        try {
            String fileName = folder + "/" + System.currentTimeMillis() + "-" + file.getOriginalFilename();
            BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
            
            // Crear el mapa de metadatos con el token de descarga
            Map<String, String> metadata = new HashMap<>();
            metadata.put("firebaseStorageDownloadTokens", UUID.randomUUID().toString());
            
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .setMetadata(metadata) // Asignar el mapa de metadatos
                    .build();

            storage.create(blobInfo, file.getInputStream());
            
            // Generar la URL de descarga autenticada
            String downloadURL = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media&token=%s",
                    BUCKET_NAME,
                    fileName.replace("/", "%2F"), // Codifica "/" en "%2F" para la URL
                    metadata.get("firebaseStorageDownloadTokens"));
            
            log.info("SUBIDA de archivo {} con URL: {}", fileName, downloadURL);
            return downloadURL;
        } catch (IOException e) {
            log.error("ERROR al subir el archivo: {}", file.getOriginalFilename(), e);    
            throw new RuntimeException("No se pudo subir el archivo " + file.getOriginalFilename(), e);
        }
    }
    
    /**
     * Borrar una imagen de la nube
     * 
     * @param URL de la imagen que se va a borrar
     * @param folder nombre de la carpeta donde está la imagen
     */
    public void eliminarArchivo(String URL, String folder) {
        String fileName = folder + "/" + obtenerNombreArchivoDesdeURL(URL);
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        boolean deleted = storage.delete(blobId);
        if (deleted) {
            log.info("Se BORRO el archivo {} exitosamente.", fileName);
        } else {
            log.error("No se pudo BORRAR el archivo {} porque no existe o no se pudo borrar.", fileName);
        }
    }
    
    /**
     * Obtiene el nombre del archivo de una URL
     * 
     * @param url
     * @return nombre del archivo
     */
    private String obtenerNombreArchivoDesdeURL(String url) {
        if (url != null)
            return url.substring(url.lastIndexOf("/") + 1);
        else
            throw new RuntimeException("URL es nulo");
    }
}
