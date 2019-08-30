package br.com.caixa.sidce.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.caixa.sidce.domain.model.enums.EventoEnum;
import br.com.caixa.sidce.util.infraestructure.domain.model.EntidadeBase;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Builder(toBuilder=true)
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper=false)
@Table(name = "DCETB038_EVENTO") 
public class Evento extends EntidadeBase<Integer> {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "NU_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "DE_EVENTO")
	@Enumerated(EnumType.STRING)
	private EventoEnum descricaoEvento;
	
	@Tolerate
	public Evento() {
		super();
	}
}
