package br.com.caixa.sidce.interfaces.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.caixa.sidce.domain.model.AgendamentoETL;
import br.com.caixa.sidce.domain.model.Arquivo;
import br.com.caixa.sidce.domain.model.SituacaoSigilo;
import br.com.caixa.sidce.domain.service.AgendamentoETLService;
import br.com.caixa.sidce.interfaces.util.FileStorage;
import br.com.caixa.sidce.interfaces.web.dto.RetornoSimbaDTO;
import br.com.caixa.sidce.util.infraestructure.auditoria.AuditoriaInterface;
import br.com.caixa.sidce.util.infraestructure.auditoria.TipoEventoAuditoriaEnum;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import br.com.caixa.sidce.util.infraestructure.log.Log;
import br.com.caixa.sidce.util.infraestructure.web.RestFullEndpoint;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/api/arquivos")
@CrossOrigin(origins = "*")
@Api(value = "AgendamentoETL")
public class AgendamentoETLController extends RestFullEndpoint<AgendamentoETL, Integer> { 

	private static final String OCTET_STREAM_TYPE = "application/octet-stream";
	private static final String ATTACHMENT = "attachment; filename=\"";
	
	@Autowired
	private Environment env;

	@Autowired
	private FileStorage fileStorage;

	@Autowired
	public AgendamentoETLController(AgendamentoETLService service) {
		super(service);
	}

	@PostMapping("/uploadArquivosTSE")
	public ResponseEntity<?> uploadMultipleFiles(Principal principal, @RequestParam("file") MultipartFile[] file,
			HttpServletRequest request) throws NegocioException {
		Log.info(this.getClass(), "Requisição de uploadMultipleFiles por: "+principal.getName());
		((AgendamentoETLService)service).uploadArquivosPartidoCandidato(file, principal.getName(), request.getLocalName());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/ultimoProcessamentoConcluido")
	public ResponseEntity<AgendamentoETL> ultimaGeracaoConcluido() throws NegocioException {
		 AgendamentoETL processo = ((AgendamentoETLService)service).buscaDadosUltimoProcessoGerado();
		if (processo == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(processo, HttpStatus.OK);
		}
	}
	
	@AuditoriaInterface(func="Geração dos 5 Arquivos", tipoEvento=TipoEventoAuditoriaEnum.DOWNLOAD)
	@GetMapping("downloadUltimoProcessamento")
	public ResponseEntity<String> downloadUltimoProcessamento(HttpServletRequest request) throws NegocioException, IOException{
		Resource resource = ((AgendamentoETLService)service).carregaOutputUltimoProcesso();		 
	    byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
	    String encodedString = Base64.getEncoder().encodeToString(bytes);
	    
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT + resource.getFilename() + "\"")
				.body(encodedString);
	} 

	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request)
			throws NegocioException {
		
		Path etlOutput = Paths.get(env.getProperty("file.etlOutputDir")).toAbsolutePath().normalize();

		// Carrega o arquivo de resposta
		Resource resource = fileStorage.carregaResourceArquivo(fileName, etlOutput);
		// Tentando definir o tipo de conteúdo da resposta
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex){
			throw new NegocioException("download-error", ex);
		}
		// Caso não seja possível definir o conteúdo, retorna o tipo default
		if (contentType == null) {
			contentType = OCTET_STREAM_TYPE;
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT + resource.getFilename() + "\"")
				.body(resource);
	} 

	@GetMapping("/situacoes")
	public ResponseEntity<List<SituacaoSigilo>> situacaoSigilo() {
		List<SituacaoSigilo> situacoes = ((AgendamentoETLService)service).buscaSituacaoSigiloDisponiveis();
		if (situacoes == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(situacoes, HttpStatus.OK);
		}
	}
	@GetMapping("/isETLDisponivel")
	public ResponseEntity<Boolean> isEnviadaSimbaUltimaGeracao() {
		boolean retorno = ((AgendamentoETLService)service).isETLDisponivel();
		return new ResponseEntity<>(retorno, HttpStatus.OK);
	}
	
	//	Operações com o arquivo emitido pelo SIMBA
	
	@AuditoriaInterface(func="Informações SIMBA", tipoEvento=TipoEventoAuditoriaEnum.INSERCAO)
	@PostMapping("/inserirRetornoSimba")
	public ResponseEntity<AgendamentoETL> inserirRetornoSimba(@RequestParam("file") MultipartFile file, HttpServletRequest request, Principal principal) throws IOException, NegocioException {
		 AgendamentoETL retorno = ((AgendamentoETLService)service).inserirArquivoSimba(file, principal.getName(), request.getParameterMap());
		 return new ResponseEntity<>(retorno, HttpStatus.OK);
	}
	
	@AuditoriaInterface(func="Informações SIMBA", tipoEvento=TipoEventoAuditoriaEnum.ALTERACAO)
	@PostMapping("/alterarRetornoSimba")
	public ResponseEntity<AgendamentoETL> alterarRetornoSimba(@RequestParam("file") MultipartFile file, HttpServletRequest request, Principal principal) throws IOException, NegocioException {
		AgendamentoETL retorno = ((AgendamentoETLService)service).alterarArquivoSimba(file, principal.getName(), request.getParameterMap());
		return new ResponseEntity<>(retorno, HttpStatus.OK);
	}
	
	@AuditoriaInterface(func="Informações SIMBA", tipoEvento=TipoEventoAuditoriaEnum.EXCLUSAO)
	@DeleteMapping("/{id}/removerRetornoSimba")
	public ResponseEntity<?> removerRetornoSimba(@PathVariable Integer id,  Principal principal) throws NegocioException {
		((AgendamentoETLService)service).removerArquivoSimba(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ResponseBody
	@GetMapping("/downloadSimbaFile/{codigo:.+}")
	@AuditoriaInterface(func="Informações SIMBA", tipoEvento=TipoEventoAuditoriaEnum.BUSCA)
	public ResponseEntity<Arquivo> buscaArquivoSimba(@PathVariable String codigo, HttpServletRequest request, Principal principal) throws NegocioException {
		Arquivo arquivo = ((AgendamentoETLService)service).buscaArquivoPorCodigo(codigo);
		return new ResponseEntity<>(arquivo, HttpStatus.OK);
	}
	
	@GetMapping("/isUltimoProcessamentoConcluido")
	public ResponseEntity<Boolean> isProntaUltimaGeracao() {
		Boolean retorno = ((AgendamentoETLService)service).isProntaUltimaGeracao();
		return new ResponseEntity<>(retorno, HttpStatus.OK);
	}
	
	@GetMapping("{id}/codigoArquivo")
	public ResponseEntity<String> buscaCodigoArquivo(@PathVariable Integer id) {
		AgendamentoETL agendamento = ((AgendamentoETLService)service).buscaAgendamentoPorSolicitacao(id);
		String retorno = agendamento.getCodigo();
		return new ResponseEntity<>(retorno, HttpStatus.OK);
	}
	
	@GetMapping("{id}/buscaAgendamento")
	public ResponseEntity<RetornoSimbaDTO> buscaAgendamentoPorSolicitacao(@PathVariable Integer id) throws NegocioException {
		RetornoSimbaDTO retorno = null;
		try {
			retorno = ((AgendamentoETLService)service).buscaRetornoSimba(id);
		} catch (NegocioException e) {
			throw new NegocioException("find-error", e);
		}
		return new ResponseEntity<>(retorno, HttpStatus.OK);
	}
	
	@GetMapping("{id}/buscaAgendamentoRetornoSimba")
	public ResponseEntity<RetornoSimbaDTO> buscaAgendamentoRetornoSimba(@PathVariable Integer id) throws NegocioException {
		RetornoSimbaDTO retorno = null;
		try {
			retorno = ((AgendamentoETLService)service).buscaRetornoSimbaArquivos(id);
		} catch (NegocioException e) {
			throw new NegocioException("find-error", e);
		}
		return new ResponseEntity<>(retorno, HttpStatus.OK);
	}
	
	@AuditoriaInterface(func="Geração dos 5 Arquivos por Afastamento", tipoEvento=TipoEventoAuditoriaEnum.DOWNLOAD)
	@GetMapping("/{id}/downloadArquivoAfastamento")
	public ResponseEntity<String> downloadArquivoPorAfastamento(@PathVariable Integer id, HttpServletRequest request, Principal principal) throws NegocioException, IOException{
		Resource resource = ((AgendamentoETLService)service).downloadArquivoPorAfastamento(id, principal);
		if(resource != null) {
			byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
		    String encodedString = Base64.getEncoder().encodeToString(bytes);
		    
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT + resource.getFilename() + "\"")
					.body(encodedString);
		}
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
}
