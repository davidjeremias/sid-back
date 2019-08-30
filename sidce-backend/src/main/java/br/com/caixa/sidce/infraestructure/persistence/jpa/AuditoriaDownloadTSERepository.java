package br.com.caixa.sidce.infraestructure.persistence.jpa;

import org.springframework.data.repository.cdi.Eager;

import br.com.caixa.sidce.domain.model.AuditoriaDownloadTSE;
import br.com.caixa.sidce.util.infraestructure.jpa.RepositoryBase;

@Eager
public interface AuditoriaDownloadTSERepository extends RepositoryBase<AuditoriaDownloadTSE, Integer>{
}
