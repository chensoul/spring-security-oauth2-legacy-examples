package com.chensoul.oauth2.common.util;

import com.chensoul.oauth2.common.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static com.chensoul.oauth2.common.util.CustomJavaTimeModule.NORM_DATETIME_PATTERN;

/**
 * @author chensoul
 * @since 1.0.0
 */
public class JsonUtils {
	private static JsonMapper jsonMapper = new JsonMapper();

	static {
		JsonMapper
			.builder()
			.serializationInclusion(JsonInclude.Include.NON_NULL)
			.findAndAddModules()
			.addModules(new CustomJavaTimeModule())
			.defaultLocale(Locale.CHINA)
			.defaultTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
			.defaultDateFormat(new SimpleDateFormat(NORM_DATETIME_PATTERN))
			// 忽略在json字符串中存在，但是在java对象中不存在对应属性的情况
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			// 允许字符串存在转义字符：\r \n \t
			.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
			.build();
	}

	private JsonUtils() {
	}

	public static JsonMapper getJsonMapper() {
		return jsonMapper;
	}

	public static String toJson(Object obj, boolean prettyPrint) {
		try {
			if (prettyPrint) {
				return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
			}
			return jsonMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new BusinessException("json序列化失败", e);
		}
	}

	public static String toJson(Object obj) {
		return toJson(obj, false);
	}

	public static void toJson(final Object obj, final OutputStream stream) {
		toJson(obj, stream, false);
	}

	public static void toJson(final Object obj, final OutputStream stream,
							  final boolean prettyPrint) {
		final JsonMapper mapper = getJsonMapper();

		try {
			if (prettyPrint) {
				final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
				writer.writeValue(stream, obj);
				return;
			}
			mapper.writeValue(stream, obj);
		} catch (IOException e) {
			throw new BusinessException("json序列化失败", e);
		}
	}

	public static void toJson(final Object obj, final File file) throws IOException {
		toJson(obj, file, false);
	}

	public static void toJson(final Object obj, final File file, final boolean prettyPrint)
		throws IOException {
		final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
		try {
			toJson(obj, stream, prettyPrint);
		} finally {
			stream.close();
		}
	}

	public static <K, V> Map<K, V> toMap(Object o) {
		if (o == null) {
			return null;
		}
		if (o instanceof String) {
			return fromJson((String) o, Map.class);
		}
		return getJsonMapper().convertValue(o, Map.class);
	}

	public static <T> List<T> toList(String json) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		try {
			return getJsonMapper().readValue(json, List.class);
		} catch (JsonProcessingException e) {
			throw new BusinessException("json读取失败", e);
		}
	}

	public static <T> List<T> toList(String json, Class<T> clazz) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		JavaType javaType = getJsonMapper().getTypeFactory().constructParametricType(List.class, clazz);

		try {
			return getJsonMapper().readValue(json, javaType);
		} catch (JsonProcessingException e) {
			throw new BusinessException("json读取失败", e);
		}
	}

	public static JsonNode getJsonField(String json, String field) {
		JsonNode jsonNode = getJsonNode(json);
		if (null == jsonNode) {
			return null;
		}
		return jsonNode.get(field);
	}

	public static JsonNode getJsonNode(String json) {
		try {
			return getJsonMapper().readTree(json);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	public static String getJsonFieldString(String json, String field) {
		JsonNode jsonNode = getJsonField(json, field);
		if (null == jsonNode) {
			return null;
		}
		return jsonNode.asText();
	}

	public static String fromJson(String json, String field) {
		JsonNode jsonNode = null;
		try {
			jsonNode = getJsonMapper().readTree(json);
		} catch (JsonProcessingException e) {
			throw new BusinessException("json读取失败", e);
		}
		return jsonNode.get(field).toString();
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		try {
			return (T) getJsonMapper().readValue(json, clazz);
		} catch (JsonProcessingException e) {
			throw new BusinessException("json读取失败", e);
		}
	}

	public static <T> T fromJson(String json, TypeReference<T> type) {
		try {
			return (T) getJsonMapper().readValue(json, type);
		} catch (JsonProcessingException e) {
			throw new BusinessException("json读取失败", e);
		}
	}


	/**
	 * 字符串转换为指定对象，并增加泛型转义 例如：List<Integer> test = fromJson(jsonStr, List.class,
	 * Integer.class);
	 *
	 * @param json             json字符串
	 * @param parametrized     目标对象
	 * @param parameterClasses 泛型对象
	 */
	public static <T> T fromJson(String json, Class<?> parametrized, Class<?>... parameterClasses) {
		if (StringUtils.isBlank(json) || parametrized == null) {
			return null;
		}
		JavaType javaType = getJsonMapper().getTypeFactory()
			.constructParametricType(parametrized, parameterClasses);

		try {
			return getJsonMapper().readValue(json, javaType);
		} catch (JsonProcessingException e) {
			throw new BusinessException("json读取失败", e);
		}
	}
}
