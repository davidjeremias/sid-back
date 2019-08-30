package br.com.caixa.sidce.interfaces.web;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.caixa.sidce.domain.model.SituacaoSolicitacao;
import br.com.caixa.sidce.domain.model.Solicitacao;
import br.com.caixa.sidce.domain.model.SolicitacaoConta;
import br.com.caixa.sidce.domain.service.SolicitacaoService;
import br.com.caixa.sidce.interfaces.util.UsuarioLogadoUtil;
import br.com.caixa.sidce.interfaces.web.dto.SolicitacaoDTO;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import br.com.caixa.sidce.util.infraestructure.log.Log;
import br.com.caixa.sidce.util.infraestructure.web.RestFullEndpoint;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/api/solicitacao")
@CrossOrigin(origins = "*")
@Api(value = "Solicitacao")
public class SolicitacaoController extends RestFullEndpoint<Solicitacao, Integer> {

	private String authorization = "Authorization";

	@Autowired
	public SolicitacaoController(SolicitacaoService service) {
		super(service);
	}

	@GetMapping("/buscaContas")
	public ResponseEntity<List<SolicitacaoConta>> buscaContas(HttpServletRequest request,
			@RequestHeader HttpHeaders headers) {
		return ((SolicitacaoService) service).buscaContasPorCpfCnpj(request.getParameterMap(),
				headers.get(this.authorization).get(0));
	}

	@GetMapping("/situacoes")
	public ResponseEntity<List<SituacaoSolicitacao>> buscaTodasSituacoes(
			@RequestParam(defaultValue = "false", required = false) Boolean isPendentes, Boolean isAfastamento) {
		List<SituacaoSolicitacao> list = ((SolicitacaoService) service).buscaSituacoes(isPendentes, isAfastamento);
		if (list.isEmpty()) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.ok(list);
		}
	}

	@PostMapping("/salvaSolicitacao")
	public ResponseEntity<Solicitacao> salvaSolicitacao(@RequestBody SolicitacaoDTO solicitacao, Principal principal,
			HttpServletRequest request) throws NegocioException {
		Solicitacao retorno = ((SolicitacaoService) service).salvarSolicitacao(solicitacao, principal.getName(),
				request.getLocalName());
		if (retorno == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(retorno, HttpStatus.CREATED);
		}
	}

	@PutMapping("/salvaSolicitacao")
	public ResponseEntity<Solicitacao> alteraSolicitacao(@RequestBody SolicitacaoDTO solicitacao, Principal principal,
			HttpServletRequest request) throws NegocioException {
		Solicitacao retorno = ((SolicitacaoService) service).salvarSolicitacao(solicitacao, principal.getName(),
				request.getLocalName());
		if (retorno == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(retorno, HttpStatus.OK);
		}
	}

	@PostMapping("/salvaAfastamento")
	public ResponseEntity<Solicitacao> salvaAfastamentoEleitoral(@RequestBody SolicitacaoDTO solicitacao,
			Principal principal, HttpServletRequest request) throws NegocioException {
		return salvarSolicitacaoPreAprovado(solicitacao, principal, request);
	}

	@PutMapping("/alterarAfastamento")
	public ResponseEntity<Solicitacao> alterarAfastamentoEleitoral(@RequestBody SolicitacaoDTO solicitacao,
			Principal principal, HttpServletRequest request) throws NegocioException {
		return salvarSolicitacaoPreAprovado(solicitacao, principal, request);
	}

	@DeleteMapping("/{id}/excluirAfastamento")
	public ResponseEntity<Solicitacao> delete(@PathVariable Integer id, Principal principal,
			HttpServletRequest request) {
		Optional<Solicitacao> solicitacao = service.findOne(id);
		if (solicitacao.isPresent()) {
			((SolicitacaoService) service).excluirAfastamento(solicitacao.get(), principal.getName(),
					request.getLocalName());
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.noContent().build();
		}
	}

	@Override
	public ResponseEntity<Page<SolicitacaoDTO>> findAllPageable(HttpServletRequest request, Principal principal)
			throws NegocioException {
		Page<SolicitacaoDTO> saida = ((SolicitacaoService) service)
				.consultaSolicitacaoPaginadoComFiltro(request.getParameterMap());
		if (saida == null || saida.getTotalElements() == 0) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(saida, HttpStatus.OK);
		}
	}

	@PostMapping("/aprovar")
	public ResponseEntity<Solicitacao> aprovar(@RequestBody Solicitacao solicitacao, Principal principal,
			HttpServletRequest request) throws NegocioException {
		Solicitacao saida = ((SolicitacaoService) service).aprovarSolicitacao(solicitacao, principal.getName(),
				request.getLocalName());
		return saida != null ? ResponseEntity.ok(saida) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping("/rejeitar")
	public ResponseEntity<Solicitacao> rejeitar(@RequestBody Solicitacao solicitacao, Principal principal,
			HttpServletRequest request) throws NegocioException {
		Solicitacao saida = ((SolicitacaoService) service).rejeitarSolicitacao(solicitacao, principal.getName(),
				request.getLocalName());
		return new ResponseEntity<>(saida, HttpStatus.OK);
	}

	private ResponseEntity<Solicitacao> salvarSolicitacaoPreAprovado(SolicitacaoDTO solicitacao, Principal principal,
			HttpServletRequest request) throws NegocioException {
		boolean isPermitido = UsuarioLogadoUtil.verificaPermissao(Arrays.asList("MATRIZ", "OPERADOR"));
		if (isPermitido) {
			Solicitacao retorno = ((SolicitacaoService) service).salvaSolicitacaoPreAprovado(solicitacao,
					principal.getName(), request.getLocalName());
			if (retorno == null) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} else {
				if (solicitacao.getRascunho()) {
					return new ResponseEntity<>(retorno, HttpStatus.OK);
				} else {
					return new ResponseEntity<>(retorno, HttpStatus.CREATED);
				}
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
		}
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handle(Exception e) {
		Log.error(this.getClass(), e);
	}
}
