package br.com.caixa.sidce.infraestructure.persistence.jpa;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.com.caixa.sidce.domain.model.Banco;
import br.com.caixa.sidce.interfaces.web.dto.VisualizacaoArquivosDTO;

public interface BancoCustomRepository{
	
	PageImpl<Banco> detalhamentoArquivoBanco(Pageable pageable, VisualizacaoArquivosDTO dto);

}
