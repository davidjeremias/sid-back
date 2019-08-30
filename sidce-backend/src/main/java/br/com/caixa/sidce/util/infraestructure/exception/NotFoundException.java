package br.com.caixa.sidce.util.infraestructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class NotFoundException extends InternalException {
 
	private static final long serialVersionUID = -1590474601955414763L;

	/**
     * Construtor padrão da exceção.
     * @param statusCode código de erro HTTP
     */
    public NotFoundException() {
        super();
    }

    /**
     * Construtor onde pode ser informada a mensagem a ser apresentada.
     * @param statusCode código de erro HTTP
     * @param msg mensagem do erro
     */
    public NotFoundException(String msg) {
        super(msg);
    }

    /**
     * Construtor onde pode ser informada a causa da exceção.
     * @param statusCode código de erro HTTP
     * @param cause causa origem da exceção lançada
     */
    public NotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Construtor onde pode ser informada a causa e a mensagem da exceção.
     * @param statusCode código de erro HTTP
     * @param msg mensagem do erro
     * @param cause causa origem da exceção lançada
     */
    public NotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
