package org.aihom;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 审批模块独立启动类
 * 
 * @author AIHOM
 * @date 2026-01-26
 */
@Slf4j
@SpringBootApplication(
    scanBasePackages = {"org.aihom", "org.jeecg.config.mybatis"},
    exclude = {
        org.apache.shiro.spring.boot.autoconfigure.ShiroAutoConfiguration.class,
        org.apache.shiro.spring.config.web.autoconfigure.ShiroWebAutoConfiguration.class,
        org.apache.shiro.spring.config.web.autoconfigure.ShiroWebFilterConfiguration.class,
        org.apache.shiro.spring.boot.autoconfigure.ShiroBeanAutoConfiguration.class,
        org.apache.shiro.spring.boot.autoconfigure.ShiroAnnotationProcessorAutoConfiguration.class
    }
)
@MapperScan(basePackages = {"org.aihom.modules.**.mapper", "org.jeecg.modules.**.mapper"})
public class ApprovalApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApprovalApplication.class);
    }

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(ApprovalApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path", "");
        
        log.info("\n----------------------------------------------------------\n\t" +
                "Application Approval-Boot is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + path + "/\n\t" +
                "External: \thttp://" + ip + ":" + port + path + "/\n\t" +
                "Swagger文档: \thttp://" + ip + ":" + port + path + "/doc.html\n" +
                "----------------------------------------------------------");
    }
}

