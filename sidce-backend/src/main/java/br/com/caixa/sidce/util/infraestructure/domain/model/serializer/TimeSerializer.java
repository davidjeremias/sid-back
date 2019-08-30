package br.com.caixa.sidce.util.infraestructure.domain.model.serializer;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import br.com.caixa.sidce.util.infraestructure.domain.model.DateUtil;

public class TimeSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(final Date value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {

        if(value != null) {
            gen.writeString(DateUtil.getParser("HH:mm").format(value));
        }

    }
}