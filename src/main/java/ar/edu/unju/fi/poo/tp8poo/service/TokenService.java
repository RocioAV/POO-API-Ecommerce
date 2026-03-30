package ar.edu.unju.fi.poo.tp8poo.service;

import ar.edu.unju.fi.poo.tp8poo.dto.ClienteDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Token;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final ClienteService clienteService;
    private final EmailService emailService;
    
    public TokenService(TokenRepository tokenRepository, ClienteService clienteService, EmailService emailService) {
    	this.tokenRepository = tokenRepository;
    	this.clienteService = clienteService;
        this.emailService = emailService;
    }

    private final Random random = new Random();


    /**
     * Busca un token asociado a un cliente específico por su ID.
     *
     * @param idCliente El ID del cliente cuyo token se busca.
     * @return El token asociado al cliente.
     * @throws NegocioException Si no se encuentra el token para el cliente.
     */
    public Token buscarPorClienteId(Long idCliente ) {
        log.debug("Buscando token para el cliente con ID: {}", idCliente);
        Optional<Token> token = tokenRepository.findByClienteId(idCliente);
        if (token.isEmpty()) {
            log.error("No se encontró el token para el cliente con ID: {}", idCliente);
            throw new NegocioException("El token no existe");
        }
        log.info("Token encontrado para el cliente con ID: {}", idCliente);
        return token.get();
    }

    /**
     * Verifica si el cliente tiene un token válido. Si el token existe y no ha expirado,
     * se lanza una excepción de negocio. Si el token ha expirado, se elimina de la base de datos.
     *
     * @param idCliente El ID del cliente cuyo token se verifica.
     * @throws NegocioException Si el cliente tiene un token válido.
     */
    public void verificarExistencia(Long idCliente ) {
        log.debug("Verificando la existencia de un token para el cliente con ID: {}", idCliente);
        Optional<Token> tokenExistente = tokenRepository.findByClienteId(idCliente);
        if (tokenExistente.isPresent()) {
            if (tokenExistente.get().getFechaExpiracion().isAfter(LocalDateTime.now())) {
                log.info("El cliente con ID {} tiene un token válido aún.", idCliente);
                throw new NegocioException("El cliente tiene un Token valido aún");
            }
            log.info("El token para el cliente con ID {} ha expirado. Procediendo a eliminarlo.", idCliente);
            tokenRepository.delete(tokenExistente.get());
        }
    }
    /**
     * Genera un nuevo token para un cliente especificado, con un valor aleatorio y una fecha de expiración.
     * Si el cliente ya tiene un token válido, lo elimina primero (si es necesario).
     *
     * @param clienteId El ID del cliente para el que se genera el token.
     * @return El nuevo token generado y guardado en la base de datos.
     * @throws NegocioException Si el cliente ya tiene un token válido.
     */
    public Token generarTokenParaCliente(Long clienteId) {
        log.info("Generando nuevo token para el cliente con ID: {}", clienteId);
        int expiracionSegundos = 120;
        String valorToken = Integer.toString(random.nextInt(1000000));
        LocalDateTime fechaExpiracion = LocalDateTime.now().plusSeconds(expiracionSegundos);
        ClienteDTO clienteDTO= clienteService.buscarPorID(clienteId);
        verificarExistencia(clienteId);
        Token token = tokenRepository.save(new Token(valorToken, fechaExpiracion, clienteId));
        log.info("Token generado y guardado para el cliente con ID: {}. Valor del token: {}", clienteId, valorToken);
        emailService.enviarTokenPorEmail(clienteDTO.getEmail(), valorToken);
        log.debug("Email enviado con el token al cliente con ID: {}", clienteId);
        return token;
    }

    /**
     * Valida el token de un cliente comprobando su valor y fecha de expiración.
     * Si el token no coincide o ha expirado, lanza una excepción de negocio.
     *
     * @param clienteId El ID del cliente cuya validación de token se realiza.
     * @param codigo El codigo del token, quien debe coincidir con el de la BD
     * @throws NegocioException Si el token es inválido o ha expirado.
     */
    public void validarToken(Long clienteId,String codigo) {
        log.debug("Validando token para el cliente con ID: {}", clienteId);
        Token token = buscarPorClienteId(clienteId);
        if (!token.getValor().equals(codigo)) {
            log.error("El código del token no coincide para el cliente con ID: {}", clienteId);
            throw new NegocioException("El codigo del token no coincide");
        }
        if (token.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            log.error("El token para el cliente con ID: {} ha expirado", clienteId);
            throw new NegocioException("El cliente tiene un Token EXPIRADO, genere uno nuevo");
        }
        log.info("Token validado correctamente para el cliente con ID: {}", clienteId);
    }


}
