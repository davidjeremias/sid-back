package br.com.caixa.sidce.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.caixa.sidce.domain.model.TipoParametro;
import br.com.caixa.sidce.infraestructure.persistence.jpa.TipoParametroRepository;
import br.com.caixa.sidce.util.infraestructure.service.RestFullService;

@Service
public class TipoParametroService extends RestFullService<TipoParametro, Integer> {

	@Autowired
	TipoParametroRepository repo;
	
	@Autowired
	TipoParametroService(TipoParametroRepository repository) {
		super(repository);
	}
}
