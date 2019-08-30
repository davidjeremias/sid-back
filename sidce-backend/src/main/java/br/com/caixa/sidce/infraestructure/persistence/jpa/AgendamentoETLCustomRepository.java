package br.com.caixa.sidce.infraestructure.persistence.jpa;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.com.caixa.sidce.domain.model.AgendamentoETL;
import br.com.caixa.sidce.interfaces.web.dto.VisualizacaoArquivosDTO;

public interface AgendamentoETLCustomRepository {

	PageImpl<AgendamentoETL> consultaAgendamentosComFiltro(Pageable pageable, Map<String, String[]> filter);
	
	boolean isArquivosGeradoUltimosTrintaDias(String codigo);

	List<AgendamentoETL> processosDisponiveisParaDownload();
	
	PageImpl<AgendamentoETL> processosGeracaoConsultaNaFila(Pageable pageable);
	
	PageImpl<VisualizacaoArquivosDTO> detalhamentoCincoArquivos(Pageable pageable, VisualizacaoArquivosDTO dto);

	List<AgendamentoETL> ultimoUploadGeracaoCadastrado();

	List<AgendamentoETL> ultimoUploadGeracaoCadastradoGerado();

	Integer gerarNumeroCodigoAfastamento();
	
}
