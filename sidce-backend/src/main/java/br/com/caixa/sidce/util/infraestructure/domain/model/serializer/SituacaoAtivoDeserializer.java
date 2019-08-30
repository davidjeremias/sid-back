package br.com.caixa.sidce.util.infraestructure.domain.model.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import br.com.caixa.sidce.util.infraestructure.domain.model.SituacaoAtivo;

public class SituacaoAtivoDeserializer extends JsonDeserializer<SituacaoAtivo> {

	private static final String A = "A";
	private static final String I = "I";

	@Override
	public SituacaoAtivo deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

		final String value = p.getText();
		if (value != null) {
			if (value.equals(A)) {
				return SituacaoAtivo.A;
			} else if (value.equals(I)) {
				return SituacaoAtivo.I;
			}
		}
		return null;
	}
}