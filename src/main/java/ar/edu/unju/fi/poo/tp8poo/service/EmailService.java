package ar.edu.unju.fi.poo.tp8poo.service;

import ar.edu.unju.fi.poo.tp8poo.dto.VentaDTO;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para enviar facturas por email.
 */
@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

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
             * Formateador para la fecha
             */
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fechaYHora = venta.getFechaYHora().format(formatter);



            /**
             * Generar el contenido HTML usando los datos de la venta
             */
            String htmlContent = htmlTemplate
                    .replace("[[${fechaYHora}]]", fechaYHora)
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
        } catch (Exception e) {
            log.error("Error al enviar la factura: {}", e.getMessage());
        }
    }
}