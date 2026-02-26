package org.aihom.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 配置
 * 
 * @author AIHOM
 * @date 2026-01-26
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi approvalApi() {
        return GroupedOpenApi.builder()
                .group("审批模块")
                .pathsToMatch("/approval/**")
                .packagesToScan("org.aihom.modules.approval")
                .build();
    }

    @Bean
    public OpenAPI aihomCustomOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AIHOM 审批系统 API")
                        .version("1.0.0")
                        .description("AIHOM 智能审批系统接口文档")
                        .contact(new Contact()
                                .name("AIHOM")
                                .email("support@aihom.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}





