package com.ambev.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;


@Configuration
public class SwaggerConfig {
	
  @Bean
  GroupedOpenApi publicApi() {
	  return GroupedOpenApi.builder()
			  .group("public-apis")
			  .pathsToMatch("/**")
			  .build();
  }
  
  @Bean
  OpenAPI customOpenAPI() {
	  return new OpenAPI()
			  .info(new Info().title("API para integração de pedidos - Ambev").version("1.0.0"));
  }
    
}