package az.zefflix.common.config;

import az.zefflix.common.exception.GlobalExceptionHandler;
import az.zefflix.common.security.JwtUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

/**
 * Zefflix Common kitabxanasının avtomatik konfiqurasiyası.
 *
 * <p>Bu sinif hər servis tərəfindən avtomatik götürülür.
 * Servisdə artıq mövcud bean varsa ({@link ConditionalOnMissingBean}),
 * override edilə bilər.
 */
@AutoConfiguration
@ConditionalOnWebApplication
public class ZeflixCommonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

}
