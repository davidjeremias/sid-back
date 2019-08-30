package br.com.caixa.sidce.util.infraestructure.domain.model.serializer;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import br.com.caixa.sidce.util.infraestructure.domain.model.DateUtil;
import br.com.caixa.sidce.util.infraestructure.log.Log;

public class TimeDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {

        final String value = p.getText();
        if(value != null) {
            try {
                return DateUtil.getParser("HH:mm").parse(value);
            } catch (ParseException ex) {
                Log.error(this.getClass(), "Erro processamento de data: " + value, ex);
                return null;
            }
        }
        return null;

    }

}