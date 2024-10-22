package ar.edu.unju.fi.poo.tp8poo.exceptions;

public class ClienteExistenteException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public ClienteExistenteException(String mensaje) {
        super(mensaje);
    }
}
