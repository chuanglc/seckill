package com.chuang.seckill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

/**
 * @Author chuang
 * @Date 2022/07/25 10:02
 **/
@Configuration
//@EnableSwagger2
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket webApiConfig(){

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("apiInfo")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                //只显示admin路径下的页面
//                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                //不显示springboot的BasicErrorControllere类接口
//                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();
    }

    private ApiInfo apiInfo(){

        return new ApiInfoBuilder()
                .title("秒杀-API文档")
                .description("本文档描述了秒杀项目系统接口")
                .version("1.0")
                .contact(new Contact("chuang", "http://baidu.com", "1596754452@qq.com"))
                .build();
    }
}