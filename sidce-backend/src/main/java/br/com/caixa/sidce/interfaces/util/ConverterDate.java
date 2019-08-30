package br.com.caixa.sidce.interfaces.util;

import java.time.*;

public class ConverterDate {
	
	public ConverterDate() {
	}
	
	private static final String TIME_ZONE = "America/Sao_Paulo";
	
	public static LocalDateTime stringToLocalDateTime(String date) {
		LocalDateTime dt = null;
		if(date != null) {
			Instant instant = Instant.parse(date);
			dt = LocalDateTime.ofInstant(instant, ZoneId.of(TIME_ZONE));
		}
		return dt;
	}

}
