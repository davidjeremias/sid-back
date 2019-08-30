package br.com.caixa.sidce.domain.model.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class SituacaoSolicitacaoEnumConverter implements AttributeConverter<SituacaoSolicitacaoEnum, String> {

	@Override
	public String convertToDatabaseColumn(SituacaoSolicitacaoEnum attribute) {
		return attribute.getLabel();
	}

	@Override
	public SituacaoSolicitacaoEnum convertToEntityAttribute(String dbData) {
		return SituacaoSolicitacaoEnum.getEnumByName(dbData);
	}

}
