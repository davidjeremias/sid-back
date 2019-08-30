package br.com.caixa.sidce.domain.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.caixa.sidce.domain.model.Auditoria;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AuditoriaCustomRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AuditoriaRepository;
import br.com.caixa.sidce.interfaces.web.dto.AuditoriaDTO;
import br.com.caixa.sidce.util.infraestructure.auditoria.TipoEventoAuditoriaEnum;
import br.com.caixa.sidce.util.infraestructure.service.RestFullService;

@Service
public class AuditoriaService extends RestFullService<Auditoria, Integer> {

	private static final String MATRICULA = "matricula";
	private static final String DT_FIM = "fimPeriodo";
	private static final String DT_INICIO = "iniPeriodo";
	private static final String EVENTO = "evento";
	private static final String FUNCIONALIDADE = "funcionalidade";
	private static final String CODIGO = "codigoSolicitacao";

	@Autowired
	private AuditoriaCustomRepository crepo;

	@Autowired
	private AuditoriaRepository repo;

	@Autowired
	AuditoriaService(AuditoriaRepository repository) {
		super(repository);
	}

	public List<String> funcionalidadesDisponiveis() {
		return repo.buscaFuncionalidadesDisponiveis();
	}

	public List<String> eventosDisponiveis(Map<String, String[]> filter) {
		String funcionalidade = (filter.get(FUNCIONALIDADE) != null ? filter.get(FUNCIONALIDADE)[0] : null);
		List<String> retorno = new ArrayList<>();
		List<TipoEventoAuditoriaEnum> lista = crepo.buscaEventosDisponiveis(funcionalidade);
		lista.forEach(e -> retorno.add(e.getNome()));
		return retorno;
	}

	public PageImpl<AuditoriaDTO> buscaAuditoriaPaginado(Map<String, String[]> filter) {
		Pageable pageable = getPageRequest(filter);
		AuditoriaDTO dto = montarDTO(filter);
		PageImpl<Auditoria> auditorias = crepo.buscaAuditoriaPaginado(pageable, dto);
		ModelMapper modelMapper = new ModelMapper();
		List<AuditoriaDTO> retorno = new ArrayList<>();
		List<Auditoria> listaAuditoria = auditorias.getContent();
		listaAuditoria.forEach(a -> {
			AuditoriaDTO auditoriaDTO = modelMapper.map(a, AuditoriaDTO.class);
			auditoriaDTO.setEvento(a.getTipoEvento().getNome());
			retorno.add(auditoriaDTO);
		});
		return new PageImpl<>(retorno, auditorias.getPageable(), auditorias.getTotalElements());
	}

	private AuditoriaDTO montarDTO(Map<String, String[]> filter) {
		Date dataInicio = null;
		Date dataFim = null;

		String matricula = (filter.get(MATRICULA) != null ? filter.get(MATRICULA)[0] : null);
		String funcionalidade = (filter.get(FUNCIONALIDADE) != null ? filter.get(FUNCIONALIDADE)[0] : null);
		String evento = (filter.get(EVENTO) != null ? filter.get(EVENTO)[0] : null);
		String dtInicio = (filter.get(DT_INICIO) != null ? filter.get(DT_INICIO)[0] : null);
		String dtFim = (filter.get(DT_FIM) != null ? filter.get(DT_FIM)[0] : null);
		String codigo = (filter.get(CODIGO) != null ? filter.get(CODIGO)[0] : null);

		if (dtInicio != null) {
			Instant instant = Instant.parse(dtInicio);
			LocalDateTime result = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
			dataInicio = Date.from(result.atZone(ZoneId.systemDefault()).toInstant());
		}
		if (dtFim != null) {
			Instant instant = Instant.parse(dtFim);
			LocalDateTime result = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
			dataFim = Date.from(result.atZone(ZoneId.systemDefault()).toInstant());
		}

		return AuditoriaDTO.builder().matricula(matricula).funcionalidade(funcionalidade).evento(evento)
				.dtInicio(dataInicio).dtFim(dataFim).codigo(codigo).build();
	}
	
}
