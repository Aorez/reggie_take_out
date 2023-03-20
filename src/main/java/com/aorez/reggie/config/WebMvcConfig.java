package com.aorez.reggie.config;

import com.aorez.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("WebMvcConfigurationSupport addResourceHandlers");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器
        //数据库中的id是19位的，Java可以接受，但是JavaScript要求是16位，另外3位数字会进行类似四舍五入的操作
        //所以需要将Java对象中的Long类型映射到json时变为String类型，所以复制了一个类过来，其中还有LocalDateTime格式的转换
        converter.setObjectMapper(new JacksonObjectMapper());
        //加入
        converters.add(0, converter);
    }
}
