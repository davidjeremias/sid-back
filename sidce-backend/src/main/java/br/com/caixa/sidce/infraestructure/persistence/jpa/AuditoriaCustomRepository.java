package br.com.caixa.sidce.infraestructure.persistence.jpa;

import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.cdi.Eager;

import br.com.caixa.sidce.domain.model.Auditoria;
import br.com.caixa.sidce.interfaces.web.dto.AuditoriaDTO;
import br.com.caixa.sidce.util.infraestructure.auditoria.TipoEventoAuditoriaEnum;

@Eager
public interface AuditoriaCustomRepository{
	
	PageImpl<Auditoria> buscaAuditoriaPaginado(Pageable pageable, AuditoriaDTO dto);

	List<TipoEventoAuditoriaEnum> buscaEventosDisponiveis(String funcionalidade);

}
