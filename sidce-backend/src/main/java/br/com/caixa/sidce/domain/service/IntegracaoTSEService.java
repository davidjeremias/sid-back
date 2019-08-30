package br.com.caixa.sidce.domain.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import br.com.caixa.sidce.domain.model.Calendario;
import br.com.caixa.sidce.domain.model.ParametrosIntegracaoTSE;
import br.com.caixa.sidce.domain.model.enums.ParametroEnum;
import br.com.caixa.sidce.infraestructure.persistence.jpa.CalendarioRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.ParametrosIntegracaoRepository;
import br.com.caixa.sidce.interfaces.util.FileStorage;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import br.com.caixa.sidce.util.infraestructure.log.Log;

@Service
public class IntegracaoTSEService  {
	
	@Autowired
	FileStorage fs;
	
	@Autowired
	CalendarioRepository calendarioRepository;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	private ParametroService parametroService;
	
	@Autowired
	ParametrosIntegracaoRepository parametrosIntegracao;
	
	public void cnpjPartidoTSE() throws NegocioException {
		String destDir = parametroService.buscarPorChave(ParametroEnum.ETL_DIR_INPUT);
		fs.unzip(buscaArquivoTSE(),destDir);
	}
	
	public LocalDate getDiaUtilMes() throws NegocioException{
		LocalDate dia = LocalDate.now();
		LocalDate diaUtil = null;
		List<Calendario> diasUteisMes = calendarioRepository.buscaDiasUteisMes(dia.getYear(), dia.getMonth().getValue());
		try {
			ParametrosIntegracaoTSE param = getUltimoParametro();
			diaUtil = diasUteisMes.get(param.getDia() -1).getData();
		} catch (NullPointerException e) {
			diaUtil = LocalDate.now().minusDays(100L);
			Log.info(this.getClass(), "Nenhum parametro encontrado para execução da rotina");
		}
		return diaUtil;
	}
	
	public LocalTime getHoraInicio() {
		ParametrosIntegracaoTSE param = getUltimoParametro();
		return param.getHora() != null ? param.getHora() : null;
	}
	
	protected Resource buscaArquivoTSE() throws NegocioException{
		ParametrosIntegracaoTSE param = getUltimoParametro();
		String url = param.getUrl();
		Resource retorno = null;
		try {
			retorno = restTemplate.getForObject(url, Resource.class);
		} catch (HttpStatusCodeException e) {
			Log.info(getClass(), "URL INVÁLIDA", e);
			throw new NegocioException("url-invalida");
		}	
		return retorno;
	}
	
	private ParametrosIntegracaoTSE getUltimoParametro() {
		return parametrosIntegracao.buscaUltimo();
	}
}
