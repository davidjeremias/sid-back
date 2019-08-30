package br.com.caixa.sidce.domain.service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import br.com.caixa.sidce.domain.model.ParametrosIntegracaoTSE;
import br.com.caixa.sidce.infraestructure.persistence.jpa.ParametrosIntegracaoRepository;
import br.com.caixa.sidce.interfaces.web.dto.ParametrosIntegracaoDTO;
import br.com.caixa.sidce.util.infraestructure.RequisicaoUtil;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import br.com.caixa.sidce.util.infraestructure.log.Log;

@Service
public class ParametrosIntegracaoService {

	private static final String URL_BASE = "http://agencia.tse.jus.br";
	private static final String ZIP_EXTENSION = ".zip";
	private static final String URL = "url";

	@Autowired
	private ParametrosIntegracaoRepository repository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public ParametrosIntegracaoTSE salvar(ParametrosIntegracaoDTO dto, Principal principal) {
		atualizaParametroAntigo(principal);
		ParametrosIntegracaoTSE novoParam = atualizaParametroNovo(dto, principal);
		return repository.save(novoParam);
	}

	public void atualizaParametroAntigo(Principal principal) {
		ParametrosIntegracaoTSE ultimo = getUltimoParametro();
		if(ultimo != null) {
			ultimo.setDataExclusao(LocalDateTime.now());
			ultimo.setMatricula(principal.getName());
			repository.save(ultimo);
		}
	}

	private ParametrosIntegracaoTSE atualizaParametroNovo(ParametrosIntegracaoDTO dto, Principal principal) {
		ModelMapper model = new ModelMapper();
		ParametrosIntegracaoTSE param = model.map(dto, ParametrosIntegracaoTSE.class);
		param.setHora(dto.getHora().toLocalTime().minusHours(3L));
		param.setMatricula(principal.getName());
		return param;
	}
	
	public List<ParametrosIntegracaoDTO> findAllParametros(){
		List<ParametrosIntegracaoTSE> lista = repository.findAll();
		List<ParametrosIntegracaoDTO> listaDTO = new ArrayList<>();
		if(!lista.isEmpty()) {
			lista.forEach(e -> {
				ParametrosIntegracaoDTO dto = new ParametrosIntegracaoDTO();
				dto.setId(e.getId());
				dto.setDia(e.getDia());
				LocalDateTime hora = LocalDateTime.of(LocalDate.now(),e.getHora());
				dto.setHora(hora);
				dto.setUrl(e.getUrl());
				listaDTO.add(dto);
			});
		}
		return listaDTO;
	}
	
	public Resource verificaLink(Map<String, String[]> filter) throws NegocioException {
		String url = RequisicaoUtil.extrairParametro(filter, URL);
		ResponseEntity<Resource> retorno = null;
		Resource resource = null;
		try {
			if(url.trim().contains(URL_BASE) && url.trim().endsWith(ZIP_EXTENSION)) {
				retorno = restTemplate.getForEntity(url.trim(), Resource.class);
				if(retorno.getStatusCode() != HttpStatus.OK) {
					throw new NegocioException("link-sem-retorno");
				}
			}else {
				retorno = new ResponseEntity<>(resource,HttpStatus.NO_CONTENT);
			}
		} catch (NegocioException | HttpStatusCodeException e) {
			Log.info(getClass(), "URL Inválida", e);
			retorno = new ResponseEntity<>(resource,HttpStatus.NO_CONTENT);
		}
		return retorno.getBody();
	}
	
	private ParametrosIntegracaoTSE getUltimoParametro() {
		return repository.buscaUltimo();
	}
}
