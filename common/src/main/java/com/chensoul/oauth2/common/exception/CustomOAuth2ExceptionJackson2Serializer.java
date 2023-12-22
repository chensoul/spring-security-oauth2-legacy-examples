package com.chensoul.oauth2.common.exception;

import com.chensoul.oauth2.common.model.RestResponse;
import com.chensoul.oauth2.common.support.RestResponseMessageResolver;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 *
 */
@Slf4j
public class CustomOAuth2ExceptionJackson2Serializer extends StdSerializer<CustomOAuth2Exception> {

	/**
	 *
	 */
	protected CustomOAuth2ExceptionJackson2Serializer() {
		super(CustomOAuth2Exception.class);
	}

	@Override
	public void serialize(CustomOAuth2Exception e, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
		RestResponse<String> errorResponse = RestResponseMessageResolver.resolver(e);
		log.error("{}", errorResponse);

		jgen.writeObject(errorResponse);
	}
}
