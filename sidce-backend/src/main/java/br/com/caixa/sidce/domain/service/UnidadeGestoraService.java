package br.com.caixa.sidce.domain.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import br.com.caixa.sidce.domain.model.UnidadeGestora;
import br.com.caixa.sidce.infraestructure.persistence.jpa.UnidadeGestoraRepository;
import br.com.caixa.sidce.interfaces.util.PageableUtil;
import br.com.caixa.sidce.interfaces.web.dto.UnidadeDTO;
import br.com.caixa.sidce.util.infraestructure.auditoria.AuditoriaAspecto;
import br.com.caixa.sidce.util.infraestructure.auditoria.TipoEventoAuditoriaEnum;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

@Service
public class UnidadeGestoraService{
	
	private static final String NUMERO_UNIDADE = "unidade";
	
	@Autowired
	private UnidadeGestoraRepository repository;
	
	@Autowired
	private SIICOService siicoService;
	
	@Autowired
	private AuditoriaAspecto auditoria;
	
	public List<UnidadeGestora> buscaUnidades() {
		return repository.findAll();
	}
	
	public UnidadeGestora salvar(UnidadeGestora unidade, String matricula) throws UnknownHostException {
		InetAddress inetAddress = InetAddress.getLocalHost();
		List<UnidadeGestora> lista = repository.findAll();
		UnidadeGestora retorno = null;
		if(lista.isEmpty()) {
			retorno = repository.save(unidade);
			auditoria.registrarAuditoria("Unidade gestora", 
					TipoEventoAuditoriaEnum.INSERCAO, 
					matricula, 
					inetAddress.getHostAddress(), 
					unidade);
		}else {
			UnidadeGestora unid = lista.get(lista.size() -1);
			retorno = repository.save(unidade);
			auditoria.registrarAuditoria("Unidade gestora", 
					TipoEventoAuditoriaEnum.ALTERACAO, 
					matricula, 
					inetAddress.getHostAddress(), 
					unid);
		}
		return retorno;
	}

	public PageImpl<UnidadeGestora> buscaUnidadePorNumero(Map<String, String[]> filter) throws NegocioException {
		Integer numeroUnidade = (filter.get(NUMERO_UNIDADE) != null ? Integer.valueOf(filter.get(NUMERO_UNIDADE)[0]) : null);
		PageImpl<UnidadeDTO> retorno = siicoService.buscaUnidade(PageableUtil.getPageRequest(filter), numeroUnidade);
		List<UnidadeGestora> unidades = new ArrayList<>();
		if(retorno == null) {
			throw new NegocioException("unidade-nao-encontrada");
		}
		retorno.getContent().forEach(e -> {
			UnidadeGestora unidade = UnidadeGestora.builder()
					.numeroUnidade(e.getUnidade())
					.descricaoUnidade(e.getNomeUnidade())
					.emailUnidade(e.getEmailUnidade()).build();
			unidades.add(unidade);
		});
		return new PageImpl<>(unidades, retorno.getPageable(), retorno.getTotalElements());
	}
}
