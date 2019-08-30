
package br.com.caixa.sidce.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.caixa.sidce.infraestructure.persistence.jpa.SIICOCustomRepository;
import br.com.caixa.sidce.interfaces.web.dto.EmailDTO;
import br.com.caixa.sidce.interfaces.web.dto.UnidadeDTO;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

@Service
public class SIICOService {

	@Autowired(required=true)
	SIICOCustomRepository siicoCustomRepository;

	public UnidadeDTO consultarUnidade(Integer unidade) throws NegocioException {

		UnidadeDTO dto = null;

		try {
			if (unidade != null) {
				dto = siicoCustomRepository.consultarUnidade(unidade);
			}
		} catch (NegocioException e) {
			throw new NegocioException("ERRO AO RECUPERAR A UNIDADE NA BASE DO SIICO", e);
		}
		return dto != null ? dto : new UnidadeDTO();
	}

	public void setSiicoCustomRepository(SIICOCustomRepository siicoCustomRepository) {
		this.siicoCustomRepository = siicoCustomRepository;
	}
	
	public PageImpl<UnidadeDTO> buscaUnidade(Pageable pageable, Integer unidade) throws NegocioException{
		if(unidade == null) {
			throw new NegocioException("unidade-nao-informada");
		}
		return siicoCustomRepository.buscaUnidades(pageable, unidade);
	}
	
	public EmailDTO buscaEmailUnidade(String matricula) throws NegocioException {
		EmailDTO email = siicoCustomRepository.buscaEmailUnidade(matricula);
		if(email == null) {
			throw new NegocioException("email-nao-encontrado");
		}
		return email;
	}

}
