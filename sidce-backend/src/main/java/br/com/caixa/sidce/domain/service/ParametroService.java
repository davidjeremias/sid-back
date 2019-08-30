package br.com.caixa.sidce.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.caixa.sidce.domain.model.Parametro;
import br.com.caixa.sidce.domain.model.enums.ParametroEnum;
import br.com.caixa.sidce.infraestructure.persistence.jpa.ParametroRepository;
import br.com.caixa.sidce.util.infraestructure.service.RestFullService;

@Service
public class ParametroService extends RestFullService<Parametro, Integer> {
	
	@Autowired
	ParametroRepository repo;
	
	@Autowired
	ParametroService(ParametroRepository repository) {
		super(repository);
	}

	public String buscarPorChave(ParametroEnum param) {
		return repo.getByChave(param).getValor();
	}
}
