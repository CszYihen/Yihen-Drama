package com.yihen.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.yihen.enums.SceneCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class JacksonConfig {

    @Bean
    public Module jacksonSceneCodeModule() {
        SimpleModule module = new SimpleModule("SceneCodeModule");
        module.addDeserializer(SceneCode.class, new JsonDeserializer<SceneCode>() {
            @Override
            public SceneCode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getValueAsString();
                if (value != null && !value.isEmpty()) {
                    try {
                        int code = Integer.parseInt(value.trim());
                        for (SceneCode sc : SceneCode.values()) {
                            if (sc.getCode() == code) {
                                return sc;
                            }
                        }
                    } catch (NumberFormatException e) {
                        for (SceneCode sc : SceneCode.values()) {
                            if (sc.name().equalsIgnoreCase(value) || 
                                sc.getKey().equalsIgnoreCase(value) ||
                                sc.getDesc().equals(value)) {
                                return sc;
                            }
                        }
                    }
                }
                throw new IllegalArgumentException("Cannot deserialize SceneCode from value: " + value);
            }
        });
        return module;
    }
}
