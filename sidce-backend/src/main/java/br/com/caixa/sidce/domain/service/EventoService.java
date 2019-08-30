package br.com.caixa.sidce.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.caixa.sidce.domain.model.Evento;
import br.com.caixa.sidce.domain.model.enums.EventoEnum;
import br.com.caixa.sidce.infraestructure.persistence.jpa.EventoRepository;
import br.com.caixa.sidce.util.infraestructure.service.RestFullService;

@Service
public class EventoService extends RestFullService<Evento, Integer> {
	
	@Autowired
	EventoRepository repo;
	
	@Autowired
	EventoService(EventoRepository repository) {
		super(repository);
	}

	public Evento buscarPorChave(EventoEnum param) {
		return repo.getByDescricaoEvento(param);
	}
}
