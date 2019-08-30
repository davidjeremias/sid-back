package br.com.caixa.sidce.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import br.com.caixa.sidce.domain.model.CodigoLancamento;
import br.com.caixa.sidce.infraestructure.persistence.jpa.CodigoLancamentoCustomRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.CodigoLancamentoRepository;
import br.com.caixa.sidce.interfaces.util.PageableUtil;
import br.com.caixa.sidce.interfaces.web.dto.CodigoLancamentoDTO;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

@Service
public class CodigoLancamentoService{
	
	@Autowired
	private CodigoLancamentoRepository repository;

	@Autowired
	private CodigoLancamentoCustomRepository crepo;
	
	private static final String CODIGO = "codigo";
	private static final String NATUREZA = "natureza";
	private static final String DESCRICAO_LANCAMENTO = "descricaoLancamento";
	private static final String IS_CODIGO = "isCodigo";
	
	public PageImpl<CodigoLancamento> buscaLancamentos(Map<String, String[]> filter){
		String descricao = (filter.get(DESCRICAO_LANCAMENTO) != null ? filter.get(DESCRICAO_LANCAMENTO)[0] : null);
		String natureza = (filter.get(NATUREZA) != null ? filter.get(NATUREZA)[0] : null);
		String codigo = (filter.get(CODIGO) != null ? filter.get(CODIGO)[0] : null);
		Boolean isCodigo = (filter.get(IS_CODIGO) != null ? Boolean.valueOf(filter.get(IS_CODIGO)[0]) : null);
		return crepo.buscaLancamentos(PageableUtil.getPageRequest(filter), descricao, natureza, codigo, isCodigo);
	}

	public CodigoLancamento salvar(CodigoLancamentoDTO dto) throws NegocioException {
		Integer codigo = (dto.getCodigoLancamento() != null ? Integer.valueOf(dto.getCodigoLancamento()) : null);
		Optional<CodigoLancamento> codigoLancamento = repository.findById(dto.getId());
		codigoLancamento.orElseThrow(() -> new NegocioException("Tipo de Lançamento não encontrado"));
		codigoLancamento.get().setCodigoLancamento(codigo);
		return repository.save(codigoLancamento.get());
	}

	public List<CodigoLancamento> salvarAll(List<CodigoLancamentoDTO> dto) {
		List<CodigoLancamento> listaCodigo = new ArrayList<>();
		dto.forEach(e -> {
			Optional<CodigoLancamento> codigoLancamento = repository.findById(e.getId());
			codigoLancamento.get().setCodigoLancamento(Integer.valueOf(e.getCodigoLancamento()));
			listaCodigo.add(codigoLancamento.get());
		});
		return repository.saveAll(listaCodigo);
	}
}
