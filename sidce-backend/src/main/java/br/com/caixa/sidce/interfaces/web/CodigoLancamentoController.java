package br.com.caixa.sidce.interfaces.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.caixa.sidce.domain.model.CodigoLancamento;
import br.com.caixa.sidce.domain.service.CodigoLancamentoService;
import br.com.caixa.sidce.interfaces.web.dto.CodigoLancamentoDTO;
import br.com.caixa.sidce.util.infraestructure.auditoria.AuditoriaInterface;
import br.com.caixa.sidce.util.infraestructure.auditoria.TipoEventoAuditoriaEnum;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/api/codigoLancamento")
@CrossOrigin(origins = "*")
@Api(value = "CodigoLancamento")
public class CodigoLancamentoController {

	@Autowired
	private CodigoLancamentoService service;

	@GetMapping("/buscar")
	public ResponseEntity<Page<CodigoLancamento>> buscaLancamentos(HttpServletRequest request) {
		Page<CodigoLancamento> retorno = service.buscaLancamentos(request.getParameterMap());
		return retorno == null || retorno.getTotalElements() == 0 ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(retorno, HttpStatus.OK);
	}

	@AuditoriaInterface(func = "Lançamentos", tipoEvento = TipoEventoAuditoriaEnum.ALTERACAO)
	@PostMapping("/salvar")
	public ResponseEntity<CodigoLancamento> salvar(@RequestBody CodigoLancamentoDTO dto) throws NegocioException {
		CodigoLancamento retorno = service.salvar(dto);
		return retorno == null ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(retorno, HttpStatus.CREATED);
	}

	@PostMapping("/salvarAll")
	public ResponseEntity<List<CodigoLancamento>> salvarAll(@RequestBody List<CodigoLancamentoDTO> dto) {
		List<CodigoLancamento> retorno = service.salvarAll(dto);
		return retorno == null ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(retorno, HttpStatus.CREATED);
	}
}
