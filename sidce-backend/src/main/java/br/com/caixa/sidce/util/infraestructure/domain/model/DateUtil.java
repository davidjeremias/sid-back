package br.com.caixa.sidce.util.infraestructure.domain.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DateUtil {
	
	public static DateFormat getParser(String formato) {
	        return new SimpleDateFormat(formato);
    }
	
	 
}
