package br.com.caixa.sidce.interfaces.web.dto;

import java.io.Serializable;

import br.com.caixa.sidce.domain.model.Banco;
import br.com.caixa.sidce.domain.model.Conta;
import br.com.caixa.sidce.domain.model.Extrato;
import br.com.caixa.sidce.domain.model.OrigemDestino;
import br.com.caixa.sidce.domain.model.Titular;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Builder(toBuilder=true)
@Getter
@Setter
public class VisualizacaoArquivosDTO implements Serializable {
	
	private static final long serialVersionUID = 1706148042303154845L;
	
	private String codigo;
	private String cnpj;
	private String numeroAgencia;
	private String numeroConta;
	private String aba;
	
	private Banco banco;
	private Conta conta;
	private Extrato extrato;
	private OrigemDestino origemDestino;
	private Titular titular;
	
	@Tolerate
	public VisualizacaoArquivosDTO(Banco banco) {
		this.banco = banco;
	}
	@Tolerate
	public VisualizacaoArquivosDTO(Conta conta) {
		this.conta = conta;
	}
	@Tolerate
	public VisualizacaoArquivosDTO(Extrato extrato) {
		this.extrato = extrato;
	}
	@Tolerate
	public VisualizacaoArquivosDTO(OrigemDestino origemDestino) {
		this.origemDestino = origemDestino;
	}
	@Tolerate
	public VisualizacaoArquivosDTO(Titular titular) {
		this.titular = titular;
	}
	@Tolerate
	public VisualizacaoArquivosDTO(Banco banco, Conta conta, Extrato extrato, OrigemDestino origemDestino,
			Titular titular) {
		super();
		this.banco = banco;
		this.conta = conta;
		this.extrato = extrato;
		this.origemDestino = origemDestino;
		this.titular = titular;
	}
	
	
}
