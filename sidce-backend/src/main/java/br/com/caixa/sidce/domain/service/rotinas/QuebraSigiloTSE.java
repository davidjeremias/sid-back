package br.com.caixa.sidce.domain.service.rotinas;

import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.caixa.sidce.domain.service.AgendamentoETLService;
import br.com.caixa.sidce.domain.service.AuditoriaDownloadTSEService;
import br.com.caixa.sidce.domain.service.IntegracaoTSEService;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import br.com.caixa.sidce.util.infraestructure.log.Log;

@Component
@EnableScheduling
public class QuebraSigiloTSE{
	
	private static final String TIME_ZONE = "America/Sao_Paulo";
	private static final Integer ERRO = 0;
	private static final Integer SUCESSO = 1;
	
	@Autowired
	IntegracaoTSEService integracaoService;
	
	@Autowired
	AgendamentoETLService agendamentoETLService;
	
	@Autowired
	AuditoriaDownloadTSEService auditoria;
	
	@Scheduled(cron = "0 0/10 * * * *", zone = TIME_ZONE)
	public void rotinaTSE() throws NegocioException, UnknownHostException {
		if (integracaoService.getDiaUtilMes().equals(LocalDate.now()) && integracaoService.getHoraInicio().getHour() == (LocalTime.now().getHour())) {
			try {
				Log.info(this.getClass(), "Rodando rotina de integração com TSE...");
				Log.info(this.getClass(), "Baixando arquivos e colocando no diretório de input do ETL...");
				integracaoService.cnpjPartidoTSE();
				Log.info(this.getClass(), "Inserindo parametros para execução do ETL...");
				agendamentoETLService.insereParametrosGeracaoRotina();
				Log.info(this.getClass(), "Rotina executada com sucesso!! Aguardando geração de arquivos pelo ETL");
				auditoria.salvaLogIntegracao(SUCESSO);
			} catch (NegocioException e) {
				auditoria.salvaLogIntegracao(ERRO);
				Log.error(this.getClass(), "Erro ao executar rotina " + e);
			}
		}
	}
}
