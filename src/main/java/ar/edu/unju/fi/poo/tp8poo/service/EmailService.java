package ar.edu.unju.fi.poo.tp8poo.service;

import ar.edu.unju.fi.poo.tp8poo.dto.VentaDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

/**
 * Servicio para enviar facturas por email.
 */
@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }
    /**
     * Envía la factura por email al cliente.
     *
     * @param venta VentaDTO que contiene los detalles de la venta.
     */
    public void enviarFacturaPorEmail(VentaDTO venta) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(venta.getCliente().getEmail());
            helper.setSubject("Factura de su compra");

            /**
             * Leer el archivo de plantilla HTML
             */
            String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/templates/facturaTemplate.html")));

            /**
             * Generar el contenido HTML usando los datos de la venta
             */
            String htmlContent = htmlTemplate
                    .replace("[[${fechaYHora}]]", venta.getFechaYHora())
                    .replace("[[${cliente.foto}]]", venta.getCliente().getFoto())
                    .replace("[[${cliente.nombre}]]", venta.getCliente().getNombre())
                    .replace("[[${cliente.apellido}]]", venta.getCliente().getApellido())
                    .replace("[[${cliente.email}]]", venta.getCliente().getEmail())
                    .replace("[[${producto.nombre}]]", venta.getProducto().getNombre())
                    .replace("[[${producto.descripcion}]]", venta.getProducto().getDescripcion())
                    .replace("[[${producto.precio}]]", String.format("%.2f", venta.getProducto().getPrecio()))
                    .replace("[[${formaPago.tipo}]]", venta.getFormaPago())
                    .replace("[[${formaPago.importe}]]", String.format("%.2f", venta.getPrecioProducto()));

            helper.setText(htmlContent, true);

            /**
             * Enviar el email
             */
            mailSender.send(message);
            log.info("Factura enviada exitosamente a {}", venta.getCliente().getEmail());
        } catch (NoSuchFileException e) {
        	throw new NegocioException("Error al leer la plantilla HTML: " + e.getMessage()); 
        } catch (Exception e) {  
        	throw new NegocioException("Error al enviar la factura: " + e.getMessage()); 
        }
    }
    
    /**
     * Envía el token por email al cliente.
     *
     * @param email del cliente y el token generado
     */
    public void enviarTokenPorEmail(String email, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Su Token de Acceso");
            /**
             * Se genera el contenido del mensaje en formato html
             */
            String htmlContent = "<p>Estimado cliente,</p>"
                    + "<p>Su token de acceso es: <strong>" + token + "</strong></p>"
                    + "<p>Este token es válido por 120 segundos.</p>";

            helper.setText(htmlContent, true);
            /**
             * Enviar el email
             */
            mailSender.send(message);
            log.info("Token enviado exitosamente a {}", email);
        } catch (Exception e) {
            throw new NegocioException("Error al enviar el token: {}" + e.getMessage());
        }
    }
}