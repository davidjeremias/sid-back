package br.com.caixa.sidce.interfaces.web;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.caixa.sidce.domain.model.AgendamentoETL;
import br.com.caixa.sidce.domain.service.AgendamentoETLConsultaService;
import br.com.caixa.sidce.interfaces.web.dto.ConsultaAgendamentoDTO;
import br.com.caixa.sidce.interfaces.web.dto.VisualizacaoArquivosDTO;
import br.com.caixa.sidce.util.infraestructure.auditoria.AuditoriaInterface;
import br.com.caixa.sidce.util.infraestructure.auditoria.TipoEventoAuditoriaEnum;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import br.com.caixa.sidce.util.infraestructure.log.Log;
import br.com.caixa.sidce.util.infraestructure.web.RestFullEndpoint;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/api/consultaArquivos")
@CrossOrigin(origins = "*")
@Api(value = "AgendamentoETLConsulta")
public class AgendamentoETLConsultaController extends RestFullEndpoint<AgendamentoETL, Integer> {

	private static final String OCTET_STREAM_TYPE = "application/octet-stream";

	@Autowired
	public AgendamentoETLConsultaController(AgendamentoETLConsultaService service) {
		super(service);
	}

	@GetMapping("/periodosInformacaoGerados")
	public ResponseEntity<List<Integer>> periodosInformacaoGerados() {

		List<Integer> lista = ((AgendamentoETLConsultaService) service).buscaPeriodosGerados();

		if (lista == null || lista.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(lista, HttpStatus.OK);
		}
	}

	@GetMapping("/executadosPorAplicacao")
	public ResponseEntity<List<AgendamentoETL>> executadosPorAplicacao() {

		List<AgendamentoETL> lista = ((AgendamentoETLConsultaService) service).executadosPorAplicacao();

		if (lista == null || lista.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(lista, HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<Page<AgendamentoETL>> findAllPageable(HttpServletRequest request, Principal principal) {
		Page<AgendamentoETL> saida = ((AgendamentoETLConsultaService) service)
				.consultaAgendamentosComFiltro(request.getParameterMap());

		if (saida == null || saida.getTotalElements() == 0) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(saida, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/solicitaGeracaoConsulta", method = RequestMethod.GET)
	@AuditoriaInterface(func = "Consulta Geração 5 Arquivos", tipoEvento = TipoEventoAuditoriaEnum.SOLICITA_GERACAO)
	public ResponseEntity<AgendamentoETL> solicitaGeracaoConsulta(Principal principal, HttpServletRequest request)
			throws NegocioException {
		String codigo = request.getParameter("codigo");
		AgendamentoETL agendamento = ((AgendamentoETLConsultaService) service).inserirParametrosConsultaGeracao(codigo,
				principal.getName(), request.getLocalName());
		return new ResponseEntity<>(agendamento, HttpStatus.OK);
	}

	@PostMapping("/downloadArquivosConsulta")
	@AuditoriaInterface(func = "Consulta Geração 5 Arquivos", tipoEvento = TipoEventoAuditoriaEnum.DOWNLOAD)
	public ResponseEntity<Resource> downloadArquivosConsulta(@RequestBody ConsultaAgendamentoDTO dto,
			Principal principal, HttpServletRequest request) throws NegocioException {

		Resource resource = ((AgendamentoETLConsultaService) service).downloadConsultaArquivo(dto.getCodigo());

		String contentType = OCTET_STREAM_TYPE;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException e) {
			throw new NegocioException("download-error", e);
		}

		// Caso não seja possível definir o conteúdo, retorna o tipo default
		if (contentType == null) {
			Log.info(this.getClass(), "Não foi possível recuperar o content-type");
			contentType = OCTET_STREAM_TYPE;
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@GetMapping("/processosGeracaoConsultaNaFila")
	public ResponseEntity<Page<AgendamentoETL>> processosGeracaoConsultaNaFila(HttpServletRequest request,
			Principal principal) {
		Page<AgendamentoETL> lista = ((AgendamentoETLConsultaService) service)
				.processosGeracaoConsultaNaFila(request.getParameterMap());
		if (lista == null || lista.getTotalElements() < 0) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(lista, HttpStatus.OK);
		}
	}

	@GetMapping("/processosDisponíveisParaDownload")
	public ResponseEntity<List<AgendamentoETL>> processosDisponiveisParaDownload() {
		List<AgendamentoETL> lista = ((AgendamentoETLConsultaService) service).listDisponivesiDownload();
		if (lista == null || lista.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(lista, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/detalhamentoCincoArquivosPaginado", method = RequestMethod.GET)
	public ResponseEntity<Page<VisualizacaoArquivosDTO>> detalhamentoCincoArquivosPaginado(HttpServletRequest request,
			Principal principal) {
		Page<VisualizacaoArquivosDTO> lista = ((AgendamentoETLConsultaService) service)
				.detalhamentoCincoArquivos(request.getParameterMap());
		if (lista == null || lista.getTotalElements() < 0) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(lista, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/detalhamentoArquivoPaginado", method = RequestMethod.GET)
	@AuditoriaInterface(func = "Detalhamento Arquivo", tipoEvento = TipoEventoAuditoriaEnum.BUSCA)
	public ResponseEntity<?> detalhamentoCincoArquivos(HttpServletRequest request, Principal principal)
			throws NegocioException {
		Page<?> lista = ((AgendamentoETLConsultaService) service)
				.detalhamentoCincoArquivosPorArquivo(request.getParameterMap());
		if (lista.getContent() == null || lista.getContent().isEmpty()) {
			return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(lista, HttpStatus.OK);
		}
	}
}
