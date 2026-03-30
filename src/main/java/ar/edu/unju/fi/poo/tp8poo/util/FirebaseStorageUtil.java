package ar.edu.unju.fi.poo.tp8poo.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
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
    private static final String[] EXTENSIONES_PERMITIDAS = {".png", ".jpg", ".jpeg", ".webp"};

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
        validarExtensionArchivo(file);
        String fileName = generarNombreArchivo(folder, file.getOriginalFilename());
        Map<String, String> metadata = generarMetadata();
        BlobInfo blobInfo  = crearBlobInfo(file,fileName,metadata);

        try {
            guardarArchivoEnBucket(blobInfo,file);
            String downloadURL = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media&token=%s",
                    BUCKET_NAME,
                    fileName.replace("/", "%2F"), // Codifica "/" en "%2F" para la URL
                    metadata.get("firebaseStorageDownloadTokens"));
            log.info("SUBIDA de archivo {} con URL: {}", fileName, downloadURL);
            return downloadURL;
        } catch (IOException e) {
            log.error("ERROR al subir el archivo: {}", file.getOriginalFilename(), e);    
            throw new NegocioException("No se pudo subir el archivo " + file.getOriginalFilename());
        }
    }

    
    /**
     * Borrar una imagen de la nube
     * 
     * @param url de la imagen que se va a borrar
     * @param folder nombre de la carpeta donde está la imagen
     */
    public void eliminarArchivo(String url, String folder) {
        String fileName = generarNombreArchivo(folder, obtenerNombreArchivoDesdeURL(url));
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
     * @param url obtiene el nombre original del archivo desde el url
     * @return nombre del archivo
     */
    private String obtenerNombreArchivoDesdeURL(String url) {
        if (url != null)
            return url.substring(url.lastIndexOf("/") + 1);
        else
            throw new NegocioException("URL es nulo");
    }

    private void validarExtensionArchivo(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new NegocioException("El archivo no tiene un nombre válido.");
        }

        boolean extensionValida = Arrays.stream(EXTENSIONES_PERMITIDAS)
                .anyMatch(extension -> fileName.toLowerCase().endsWith(extension));

        if (!extensionValida) {
            throw new NegocioException("El archivo no tiene una extensión permitida. Extensiones válidas: .png, .jpg, .jpeg, .webp");
        }
    }
    /**
     * Genera un nombre único para el archivo.
     *
     * @param folder            nombre de la carpeta
     * @param originalFilename  nombre original del archivo
     * @return nombre generado
     */
    private String generarNombreArchivo(String folder, String originalFilename) {
        return folder + "/" + System.currentTimeMillis() + "-" + originalFilename;
    }

    /**
     * Genera los metadatos necesarios para un archivo que será subido a Firebase Storage.
     * Incluye un token único para la descarga autenticada.
     *
     * @return un mapa con los metadatos del archivo
     */
    private Map<String, String> generarMetadata() {
        log.debug("Generando metadatos para el archivo.");
        Map<String, String> metadata = new HashMap<>();
        String token = UUID.randomUUID().toString();
        metadata.put("firebaseStorageDownloadTokens", token);
        log.debug("Metadatos generados con token: {}", token);
        return metadata;
    }

    /**
     * Crea un objeto BlobInfo que representa la configuración del archivo en Firebase Storage.
     *
     * @param file      el archivo que será subido
     * @param fileName  el nombre con el que se guardará el archivo en el bucket
     * @param metadata  los metadatos que se asociarán al archivo
     * @return un objeto BlobInfo configurado para el archivo
     */
    private BlobInfo crearBlobInfo(MultipartFile file, String fileName, Map<String, String> metadata) {
        log.debug("Creando BlobInfo para el archivo: {}", fileName);
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .setMetadata(metadata)
                .build();
        log.debug("BlobInfo creado para el archivo: {}", fileName);
        return blobInfo;
    }
    /**
     * Guarda un archivo en el bucket de Firebase Storage usando la configuración proporcionada.
     *
     * @param blobInfo la configuración del archivo en Firebase Storage
     * @param file     el archivo que será subido
     * @throws IOException si ocurre un error al leer el archivo
     */
    private void guardarArchivoEnBucket(BlobInfo blobInfo, MultipartFile file) throws IOException {
        log.debug("Iniciando subida del archivo al bucket: {}", blobInfo.getBlobId().getName());
        storage.create(blobInfo, file.getInputStream());
        log.info("Archivo subido exitosamente al bucket: {}", blobInfo.getBlobId().getName());
    }

}
