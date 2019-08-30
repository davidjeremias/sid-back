package br.com.caixa.sidce.domain.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.caixa.sidce.util.infraestructure.domain.model.EntidadeBase;
import br.com.caixa.sidce.util.infraestructure.domain.model.serializer.DataHoraDeserializer;
import br.com.caixa.sidce.util.infraestructure.domain.model.serializer.DataHoraSerializer;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Builder
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper=false)
@Table(name = "DCETB026_CALENDARIO")
public class Calendario extends EntidadeBase<Integer> {

	private static final long serialVersionUID = 4247397696681453810L;

	@Id
	@Column(name = "NU_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonSerialize(using = DataHoraSerializer.class)
	@JsonDeserialize(using = DataHoraDeserializer.class)
	@Column(name = "DT_DATA", nullable = true)
	private LocalDate data;
	
	@Column(name = "IC_DIA_UTIL")	//	Indicador que aponta se a data é referente a dia útil.
	private String diaUtil;
	
	@Column(name = "IC_FERIADO")
	private String feriado;
	
	@Column(name = "IC_FIM_DE_SEMANA")
	private String finalDeSemana;
	
	@Column(name = "AA_ANO")
	private int ano;
	
	@Column(name = "MM_MES")
	private int mes;

	@Tolerate
	public Calendario() {
		super();
	}
}
