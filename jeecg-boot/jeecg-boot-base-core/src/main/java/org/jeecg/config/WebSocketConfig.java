package org.jeecg.config;

import org.jeecg.config.filter.WebsocketFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Description: WebSocketConfig
 * @author: jeecg-boot
 */
@Configuration
public class WebSocketConfig {
    /**
     * 	注入ServerEndpointExporter，
     * 	这个bean会自动注册使用了@ServerEndpoint注解声明的Websocket endpoint
     *  注意：如果在IDEA中运行出现 "ServerContainer not available" 错误，
     *  可以在application-dev.yml中添加 jeecg.websocket.enabled: false 来禁用
     */
    @Bean
    @ConditionalOnProperty(name = "jeecg.websocket.enabled", havingValue = "true", matchIfMissing = true)
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    public WebsocketFilter websocketFilter(){
        return new WebsocketFilter();
    }

    @Bean
    public FilterRegistrationBean getFilterRegistrationBean(){
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(websocketFilter());
        //TODO 临时注释掉，测试下线上socket总断的问题
        bean.addUrlPatterns("/taskCountSocket/*", "/websocket/*","/eoaSocket/*","/eoaNewChatSocket/*", "/newsWebsocket/*", "/dragChannelSocket/*", "/vxeSocket/*");
        return bean;
    }

}
