package cat.iesmanacor.gestibgsuite.config;

import cat.iesmanacor.gestibgsuite.handler.TokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Bean
    public TokenInterceptor getInterceptor() {
        return new TokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] excluded = {"/auth/**/*"};
        registry.addInterceptor(getInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(excluded);
    }
}