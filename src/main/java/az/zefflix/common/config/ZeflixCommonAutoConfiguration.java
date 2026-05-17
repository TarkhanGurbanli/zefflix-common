package az.zefflix.common.config;

import az.zefflix.common.exception.GlobalExceptionHandler;
import az.zefflix.common.security.JwtProperties;
import az.zefflix.common.security.JwtUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Zefflix Common kitabxanasının avtomatik konfiqurasiyası.
 *
 * <p>Bu sinif hər servis tərəfindən avtomatik götürülür.
 * Servisdə artıq mövcud bean varsa ({@link ConditionalOnMissingBean}),
 * override edilə bilər.
 *
 * <p>FIX: {@link JwtUtil} artıq boş constructor ilə yaradılmır.
 * {@link JwtProperties} vasitəsilə secret key application.yml-dən alınır.
 */
@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties(JwtProperties.class)
public class ZeflixCommonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    /**
     * FIX: JwtUtil requires JwtProperties — secret key must be configured.
     * If the consuming service defines its own JwtUtil bean, this one is skipped.
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtUtil jwtUtil(JwtProperties jwtProperties) {
        return new JwtUtil(jwtProperties);
    }

}
