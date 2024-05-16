package com.egatrap.partage.common.config;

import com.egatrap.partage.common.util.DateTimeFormatterAdapter;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;

@Configuration
public class AppConfig {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(DateTimeFormatter.class, new DateTimeFormatterAdapter())
                .setFieldNamingStrategy(new FieldNamingStrategy() {
                    //                    convert camelCase to snake_case
                    @Override
                    public String translateName(Field f) {
                        return FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.translateName(f);
                    }
                })
                .create();
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
//        Entity에 Setter 가 없어도 작동하도록 설정
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        return modelMapper;
    }

}
