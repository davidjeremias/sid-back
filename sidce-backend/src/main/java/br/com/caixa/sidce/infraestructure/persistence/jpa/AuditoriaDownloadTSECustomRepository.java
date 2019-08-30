package br.com.caixa.sidce.infraestructure.persistence.jpa;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.caixa.sidce.domain.model.AuditoriaDownloadTSE;

@Repository
public interface AuditoriaDownloadTSECustomRepository {

	PageImpl<AuditoriaDownloadTSE> buscaAuditoriaRotina(Pageable pageable, LocalDateTime inicio, LocalDateTime fim, Integer status);
}
