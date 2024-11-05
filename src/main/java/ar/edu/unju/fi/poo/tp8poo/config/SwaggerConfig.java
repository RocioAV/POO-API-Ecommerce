package ar.edu.unju.fi.poo.tp8poo.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;



@OpenAPIDefinition(
        info = @Info(
                title = "API NEGOCIO - GRUPO 2",
                description="Nuestra aplicacion provee gestion del negocio, ya sea de cliente, producto, proveedor y sus ventas",
                version = "1.0.0",
                contact = @Contact(
<<<<<<< src/main/java/ar/edu/unju/fi/poo/tp8poo/config/SwaggerConfig.java
                        name = "Velazques, Rocio Alejandra.     " +
                                "Valle, Alejandro Leonel.     " +
                                "Bazan, Fabricio Agustín",
=======
>>>>>>> src/main/java/ar/edu/unju/fi/poo/tp8poo/config/SwaggerConfig.java
                        email = "44351449@fi.unju.edu.ar, alejandrovalle1904@gmail.com, 41300614@fi.unju.edu.ar"
                ),
                license = @License(
                        name = "Standard software Use Licence for Grupo2"
                )

        ),
        servers = {
                @Server(
                        description = "DEV SERVER",
                        url = "http://localhost:9000"
                )
        }
)


public class SwaggerConfig {}



