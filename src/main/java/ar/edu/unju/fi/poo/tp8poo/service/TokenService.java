package ar.edu.unju.fi.poo.tp8poo.service;

import ar.edu.unju.fi.poo.tp8poo.dto.ClienteDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Token;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.repository.TokenRepository;
import ar.edu.unju.fi.poo.tp8poo.service.EmailService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final ClienteService clienteService;
    private final EmailService emailService;
    
    public TokenService(TokenRepository tokenRepository, 
    		ClienteService clienteService,
    		EmailService emailService) {
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
        Optional<Token> token = tokenRepository.findByClienteId(idCliente);
        if (token.isEmpty()) {
            throw new NegocioException("El token no existe");
        }
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
        Optional<Token> tokenExistente = tokenRepository.findByClienteId(idCliente);
        if (tokenExistente.isPresent()) {
            if (tokenExistente.get().getFechaExpiracion().isAfter(LocalDateTime.now())) {
                throw new NegocioException("El cliente tiene un Token valido aún");
            }
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
        String valorToken = Integer.toString(random.nextInt(1000000));
        ClienteDTO clienteDTO= clienteService.buscarPorID(clienteId);   
        int expiracionSegundos = 120;
        LocalDateTime fechaExpiracion = LocalDateTime.now().plusSeconds(expiracionSegundos);

        verificarExistencia(clienteId);

        Token token = new Token(valorToken, fechaExpiracion, clienteId);
        token = tokenRepository.save(token);
        emailService.enviarTokenPorEmail(clienteDTO.getEmail(), valorToken);
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
        Token token = buscarPorClienteId(clienteId);
        if (!token.getValor().equals(codigo)) {
            throw new NegocioException("El codigo del token no coincide");
        }
        if (token.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new NegocioException("El cliente tiene un Token EXPIRADO, genere uno nuevo");
        }

    }


}
