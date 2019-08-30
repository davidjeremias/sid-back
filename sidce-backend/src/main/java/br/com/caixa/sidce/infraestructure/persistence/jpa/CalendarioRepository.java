package br.com.caixa.sidce.infraestructure.persistence.jpa;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.data.repository.query.Param;

import br.com.caixa.sidce.domain.model.Calendario;
import br.com.caixa.sidce.util.infraestructure.jpa.RepositoryBase;

@Eager
public interface CalendarioRepository extends RepositoryBase<Calendario, Integer> {

	@Query("SELECT c FROM Calendario c WHERE data = ?1")
	Calendario buscaDadosDia(LocalDate data);
	
	@Query("SELECT c FROM Calendario c WHERE c.ano = :ano AND c.mes = :mes AND c.diaUtil = 'S' AND c.feriado = 'N' AND c.finalDeSemana = 'N'")
	List<Calendario> buscaDiasUteisMes(@Param("ano") int ano, @Param("mes") int mes);
}
