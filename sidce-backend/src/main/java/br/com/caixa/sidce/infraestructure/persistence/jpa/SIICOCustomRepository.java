package br.com.caixa.sidce.infraestructure.persistence.jpa;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.com.caixa.sidce.interfaces.web.dto.EmailDTO;
import br.com.caixa.sidce.interfaces.web.dto.UnidadeDTO;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

public interface SIICOCustomRepository {

	UnidadeDTO consultarUnidade(Integer unidade) throws NegocioException;

	PageImpl<UnidadeDTO> buscaUnidades(Pageable pageRequest, Integer numeroUnidade) throws NegocioException;
	
	EmailDTO buscaEmailUnidade(String matricula) throws NegocioException;

}
