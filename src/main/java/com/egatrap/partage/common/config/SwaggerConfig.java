package com.egatrap.partage.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2     // Swagger2 버전 활성화 어노테이션
public class SwaggerConfig {

    // API 문서 기본 정보 설정
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Partage Rest API Documentation")
                .description("parage rest api documentation")
                .version("v1.0.0")
                .build();
    }

    // Swagger 설정 구성
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)         // OpenAPI 3.0
                .useDefaultResponseMessages(true)     // Swagger 기본 응답 메시지 사용 여부, true
                .apiInfo(apiInfo())                         // API 문서 기본 정보
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.egatrap.partage"))   // base package 설정
                .paths(PathSelectors.any())                                         // path 지정, 현재는 모든 path API 추가
                .build()
                .securitySchemes(List.of(apiKey()))
                .securityContexts(Collections.singletonList(securityContext()));
    }

    //
    private ApiKey apiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Authorization", authorizationScopes));
    }
}
