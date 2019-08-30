package br.com.caixa.sidce.util.infraestructure.domain.model.serializer;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import br.com.caixa.sidce.util.infraestructure.domain.model.DateUtil;
import br.com.caixa.sidce.util.infraestructure.log.Log;

public class DataHoraDeserializer extends JsonDeserializer<Date> {
	
	private static final String DATE_PATTERN = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
	private static final String DATE_HOUR_PATTERN = DATE_PATTERN + "\\s[0-9]{2}:[0-9]{2}";

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
    	
        final String value = p.getText();
        if(value != null && value.length() > 0) {
            try {
            	if(value.matches(DATE_HOUR_PATTERN)) {            		
            		return DateUtil.getParser("yyyy-MM-dd HH:mm").parse(value);
            	} else if (value.matches(DATE_PATTERN)) {
            		return DateUtil.getParser("yyyy-MM-dd").parse(value);
            	} else {
            		throw new ParseException("Erro processamento de data", 0);
            	}
            } catch (ParseException ex) {
                Log.error(this.getClass(), "Erro processamento de data: " + value, ex);
                return null;
            }
        }
        return null;
    }

}