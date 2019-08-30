package br.com.caixa.sidce.interfaces.web.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsultaAgendamentoDTO {
	
	private Integer id;
	private String solicitante;
	private Integer periodo;
	private Date dataUpload;
	private Date dataGeracaoArquivo;
	private String inicioDataGeracaoArquivo;
	private String fimDataGeracaoArquivo;
	private String codigo;
	
}
