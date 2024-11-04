package ar.edu.unju.fi.poo.tp8poo.util;

import java.io.IOException;

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

	private static final String BUCKET_NAME = "tp8poo2024.firebasestorage.app";

    private final Storage storage;

    // Injección de dependencia
    public FirebaseStorageUtil(FirebaseApp firebaseApp) {
        this.storage = StorageClient.getInstance(firebaseApp).bucket().getStorage();
    }
    
	
    /**
     * Subida de archivos a la nube de firebase.
     * Se genera un nombre para el archivo, se crea un BlobInfo
     * que se subira y creara dentro del bucket en la nube y se 
     * devuelve la url del archivo subido.
     * 
     * @param file archivo a subir
     * @param folder nombre de la carpeta en la que se guardara
     * @throws RuntimeException si hay fallo con la subida del archivo
     */
    public String subirArchivo(MultipartFile file, String folder) {
        try {
	        String fileName = folder + "/" + System.currentTimeMillis() + "-" + file.getOriginalFilename();
	        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(BUCKET_NAME, fileName))
	                .setContentType(file.getContentType())
	                .build();
	        
	        storage.create(blobInfo, file.getInputStream());

            String downloadURL = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    BUCKET_NAME, fileName.replace("/", "%2F"));
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
     * @param folder nombre de la carpeta donde esta la imagen
     */
    public void eliminarArchivo(String url, String folder) {
        String fileName = folder + "/" + obtenerNombreArchivoDesdeURL(url);
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        boolean deleted = storage.delete(blobId);
        if (deleted) {
        	log.info("Se BORRO el archivo {} exitosamente.", fileName);
        } else {
        	log.error("No se pudo BORRAR el archivo {} porque no existe o no se pudo borrar.", fileName);
        }
    }
	
    /**
     * Obtiene el nombre del archivo de una url
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
