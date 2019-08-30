package br.com.caixa.sidce.domain.service;

import static br.com.caixa.sidce.interfaces.util.ConverterDate.stringToLocalDateTime;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import br.com.caixa.sidce.domain.model.*;
import br.com.caixa.sidce.infraestructure.persistence.jpa.*;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import br.com.caixa.sidce.util.infraestructure.service.RestFullService;

@Service
public class AuditoriaDownloadTSEService extends RestFullService<AuditoriaDownloadTSE, Integer> {

	private static final String INICIO = "inicio";
	private static final String FIM = "fim";
	private static final String STATUS = "status";

	@Autowired
	private AuditoriaDownloadTSECustomRepositoryImpl crepo;

	@Autowired
	private AuditoriaDownloadTSERepository repo;

	@Autowired
	private ParametrosIntegracaoRepository parametrosIntegracao;

	@Autowired
	public AuditoriaDownloadTSEService(AuditoriaDownloadTSERepository repository) {
		super(repository);
	}

	public void salvaLogIntegracao(Integer status) throws NegocioException {
		ParametrosIntegracaoTSE atual = parametrosIntegracao.buscaUltimo();
		if (atual == null) {
			throw new NegocioException("parametro-nao-encontrado");
		}
		AuditoriaDownloadTSE auditoria = AuditoriaDownloadTSE.builder().dataHoraProcessamento(LocalDateTime.now())
				.status(status).parametrosIntegracaoTSE(atual).build();
		repo.save(auditoria);
	}

	public PageImpl<AuditoriaDownloadTSE> buscaAuditoriaRotina(Map<String, String[]> filter) {
		Pageable pageable = getPageRequest(filter);
		LocalDateTime inicio = stringToLocalDateTime((filter.get(INICIO) != null ? filter.get(INICIO)[0] : null));
		LocalDateTime fim = stringToLocalDateTime((filter.get(FIM) != null ? filter.get(FIM)[0] : null));
		Integer status = null;
		if (filter.get(STATUS) != null) {
			status = Integer.valueOf((filter.get(STATUS)[0]));
		}
		return crepo.buscaAuditoriaRotina(pageable, inicio, fim, status);
	}

	public AuditoriaDownloadTSE buscaParametrosAtual(Integer id) throws NegocioException {
		Optional<AuditoriaDownloadTSE> auditoria = repo.findById(id);
		return auditoria.orElseThrow(() -> new NegocioException("parametros-nao-encontrado"));
	}

}
