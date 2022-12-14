package top.mrys.custom;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {
  public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
  public static final String YYYY_MM_DD = "yyyy-MM-dd";
  public static final String HH_MM_SS = "HH:mm:ss";
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer customizer() {
    return builder -> {
      ObjectMapper objectMapper = new ObjectMapper();

//    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
      objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      objectMapper.enable(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS);
      objectMapper.disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
      objectMapper.disable(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS);

      SimpleModule module = new SimpleModule();

      module.addSerializer(Long.class, ToStringSerializer.instance);
      module.addSerializer(Long.TYPE, ToStringSerializer.instance);
      objectMapper.registerModule(module);

      JavaTimeModule javaTimeModule = new JavaTimeModule();

      //???????????????
      javaTimeModule.addSerializer(LocalDateTime.class,
        new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS)));
      javaTimeModule.addSerializer(LocalDate.class,
        new LocalDateSerializer(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
      javaTimeModule.addSerializer(LocalTime.class,
        new LocalTimeSerializer(DateTimeFormatter.ofPattern(HH_MM_SS)));

      //??????????????????
      javaTimeModule.addDeserializer(LocalDateTime.class,
        new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS)));
      javaTimeModule.addDeserializer(LocalDate.class,
        new LocalDateDeserializer(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
      javaTimeModule.addDeserializer(LocalTime.class,
        new LocalTimeDeserializer(DateTimeFormatter.ofPattern(HH_MM_SS)));
      objectMapper.registerModule(javaTimeModule);

      objectMapper.setDateFormat(new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS));

      builder.simpleDateFormat("yyyy-MM-dd HH:mm:ss");
      builder.timeZone("GMT+8");
      builder.configure(objectMapper);
    };
  }
}
