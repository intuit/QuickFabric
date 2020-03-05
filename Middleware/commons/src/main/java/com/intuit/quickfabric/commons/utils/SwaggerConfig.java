package com.intuit.quickfabric.commons.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Component
@ComponentScan(basePackages = {"com.intuit.quickfabric"})
public class SwaggerConfig 
{
	
	 @Bean
	    public Docket api() { 
		 
		/* ParameterBuilder aParameterBuilder = new ParameterBuilder();
	        aParameterBuilder.name("Authorization")                 // name of header
	                         .modelRef(new ModelRef("string"))
	                         .parameterType("header")               // type - header
	                              // based64 of - zone:mypassword
	                         .required(true)                // for compulsory
	                         .build();
	        java.util.List<Parameter> aParameters = new ArrayList<>();
	        aParameters.add(aParameterBuilder.build()); 
	        
	        
	        */
	        
	        return new Docket(DocumentationType.SWAGGER_2)  
	          .select()                                  
	          .apis(RequestHandlerSelectors.any())              
	          .paths(PathSelectors.any())                          
	          .build() ;
	        //.globalOperationParameters(aParameters);                                         
	    }
	 
	 

}
