package emazon.cart.infrastructure.config.feign;

import emazon.cart.infrastructure.config.feign.interceptor.JwtRequestInterceptor;
import feign.Client;
import feign.Logger;
import feign.RequestInterceptor;
import feign.httpclient.ApacheHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Client feignClient() {
        return new ApacheHttpClient();
    }

    @Bean
    public RequestInterceptor requestInterceptor(){
        return new JwtRequestInterceptor();
    }

    @Bean
    public Logger .Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    }