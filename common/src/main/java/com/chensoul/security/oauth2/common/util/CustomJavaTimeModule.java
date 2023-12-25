
package com.chensoul.security.oauth2.common.util;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author chensoul
 * @since 1.0.0
 */
public class CustomJavaTimeModule extends SimpleModule {
	public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String NORM_DATE_PATTERN = "yyyy-MM-dd";
	public static final String NORM_TIME_PATTERN = "HH:mm:ss";

	public CustomJavaTimeModule() {
		super(PackageVersion.VERSION);

		this.addSerializer(LocalDateTime.class,
			new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN)));
		this.addSerializer(LocalDate.class,
			new LocalDateSerializer(DateTimeFormatter.ofPattern(NORM_DATE_PATTERN)));
		this.addSerializer(LocalTime.class,
			new LocalTimeSerializer(DateTimeFormatter.ofPattern(NORM_TIME_PATTERN)));
		this.addSerializer(Instant.class, InstantSerializer.INSTANCE);

		this.addDeserializer(LocalDateTime.class,
			new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN)));
		this.addDeserializer(LocalDate.class,
			new LocalDateDeserializer(DateTimeFormatter.ofPattern(NORM_DATE_PATTERN)));
		this.addDeserializer(LocalTime.class,
			new LocalTimeDeserializer(DateTimeFormatter.ofPattern(NORM_TIME_PATTERN)));
		this.addDeserializer(Instant.class, InstantDeserializer.INSTANT);
	}
}
