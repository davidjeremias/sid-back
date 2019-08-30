package br.com.caixa.sidce.infraestructure.persistence.jpa;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.com.caixa.sidce.domain.model.Conta;
import br.com.caixa.sidce.interfaces.web.dto.VisualizacaoArquivosDTO;

public interface ContaCustomRepository{
	
	PageImpl<Conta> detalhamentoArquivoConta(Pageable pageable, VisualizacaoArquivosDTO dto);

}
