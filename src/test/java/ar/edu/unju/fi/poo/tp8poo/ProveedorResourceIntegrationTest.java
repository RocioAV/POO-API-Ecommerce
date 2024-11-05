package ar.edu.unju.fi.poo.tp8poo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
public class ProveedorResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testObtenerProveedores() throws Exception {
        mockMvc.perform(get("/api/v1/proveedor/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proveedores", hasSize(3)))
                .andExpect(jsonPath("$.proveedores[0].nombre").value("Proveedor A"))
                .andExpect(jsonPath("$.proveedores[1].nombre").value("Proveedor B"))
                .andExpect(jsonPath("$.proveedores[2].nombre").value("Proveedor C"));
    }
}
