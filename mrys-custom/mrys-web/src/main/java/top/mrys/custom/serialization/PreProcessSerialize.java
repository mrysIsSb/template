package top.mrys.custom.serialization;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.IOException;
import java.util.Objects;

/**
 * @author mrys
 */
public class PreProcessSerialize extends JsonSerializer<Object> implements ApplicationContextAware,
        ContextualSerializer {

    private PreSerializeProcess preProcess;
    private static final ExpressionParser parser = new SpelExpressionParser();
    private static ApplicationContext applicationContext;

    public PreProcessSerialize() {

    }

    public PreProcessSerialize(PreSerializeProcess preProcess) {
        this.preProcess = preProcess;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (!Objects.isNull(value) || (preProcess != null && preProcess.nullable())) {
            if (BeanUtil.isBean(value.getClass())) {
                serializers.findValueSerializer(value.getClass()).serialize(value, gen, serializers);
                return;
            }
            String el = preProcess.value();
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setBeanResolver(new BeanFactoryResolver(applicationContext));
            context.setVariable("value", value);
            gen.writeObject(parser.parseExpression(el).getValue(context));
            return;

        }
        serializers.findNullValueSerializer(null);
    }


    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property != null) { // 为空直接跳过
            PreSerializeProcess preProcess = property.getAnnotation(PreSerializeProcess.class);
            if (preProcess == null) {
                preProcess = property.getContextAnnotation(PreSerializeProcess.class);
            }
            if (preProcess != null) {
                return new PreProcessSerialize(preProcess);
            }
            return prov.findValueSerializer(property.getType(), property);
        }
        return prov.findNullValueSerializer(property);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        PreProcessSerialize.applicationContext = applicationContext;
    }
}
