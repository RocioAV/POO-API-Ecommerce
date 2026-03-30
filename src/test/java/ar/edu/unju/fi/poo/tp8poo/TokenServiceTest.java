package ar.edu.unju.fi.poo.tp8poo;

import ar.edu.unju.fi.poo.tp8poo.entity.Token;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.repository.TokenRepository;
import ar.edu.unju.fi.poo.tp8poo.service.TokenService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;

    private static Long clienteId;
    private static Token tokenValido;
    private static Token tokenExpirado;

    @BeforeEach
     void setUp() {
        clienteId = 1L;
        tokenValido = new Token("123456", LocalDateTime.now().plusSeconds(120), clienteId);
        tokenExpirado = new Token("654321", LocalDateTime.now().minusSeconds(120), 2L);
        tokenRepository.save(tokenValido);
        tokenRepository.save(tokenExpirado);
    }

    @Test
     void testBuscarPorClienteIdCuandoTokenExiste() {
        Token token = tokenService.buscarPorClienteId(clienteId);

        assertNotNull(token);
        assertEquals(tokenValido.getValor(), token.getValor());
    }

    @Test
     void testBuscarPorClienteIdCuandoTokenNoExiste() {
        Long otroClienteId = 5L;

        NegocioException exception = assertThrows(NegocioException.class, () -> {
            tokenService.buscarPorClienteId(otroClienteId);
        });

        assertEquals("El token no existe", exception.getMessage());
    }

    @Test
     void testVerificarExistenciaTokenValido() {
        NegocioException exception = assertThrows(NegocioException.class, () -> {
            tokenService.verificarExistencia(clienteId);
        });

        assertEquals("El cliente tiene un Token valido aún", exception.getMessage());
    }

    @Test
     void testVerificarExistenciaTokenExpiradoYEliminar() {

        tokenService.verificarExistencia(2L);

        Token tokenEliminado = tokenRepository.findByClienteId(2L).orElse(null);
        assertNull(tokenEliminado);
    }

    @Test
     void testGenerarTokenParaCliente() {
        tokenRepository.delete(tokenValido);

        Token nuevoToken = tokenService.generarTokenParaCliente(clienteId);

        assertNotNull(nuevoToken);
        assertNotEquals(tokenValido.getValor(), nuevoToken.getValor());
    }

    @Test
     void testValidarTokenExpirado() {
        NegocioException exception = assertThrowsExactly(NegocioException.class, () -> {
            tokenService.validarToken(2L,tokenExpirado.getValor());
        });

        assertEquals("El cliente tiene un Token EXPIRADO, genere uno nuevo", exception.getMessage());
    }
    @Test
    void testValidarTokenCodigoInvalido(){
        NegocioException exception = assertThrows(NegocioException.class, () -> {
            tokenService.validarToken(2L,"546872");
        });
        assertEquals("El codigo del token no coincide", exception.getMessage());
    }

}
