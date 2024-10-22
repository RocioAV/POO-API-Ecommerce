package ar.edu.unju.fi.poo.tp8poo.exceptions;

public class ClienteInexixtenteExcepcion extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClienteInexixtenteExcepcion(String mensaje) {
        super(mensaje);
    }
}
