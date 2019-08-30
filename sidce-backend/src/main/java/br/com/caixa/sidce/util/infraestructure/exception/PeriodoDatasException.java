package br.com.caixa.sidce.util.infraestructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value=HttpStatus.UNPROCESSABLE_ENTITY)
public class PeriodoDatasException extends NegocioException {

	private static final String MSG = "dataFim-maior-dataInicio";

	private static final long serialVersionUID = 1L;

	/**
     * Construtor padrão da exceção.
     * @param statusCode código de erro HTTP
     */
    public PeriodoDatasException() {
        super(MSG);
    }

}
