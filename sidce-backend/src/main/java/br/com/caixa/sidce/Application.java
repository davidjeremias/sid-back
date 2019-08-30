package br.com.caixa.sidce;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import br.com.caixa.sidce.config.WebConfig;
import br.com.caixa.sidce.config.WebSecurityConfig;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootApplication(scanBasePackages = {
		"br.com.caixa.sidce.config",
	    "br.com.caixa.sidce.interfaces.web",
	    "br.com.caixa.sidce.domain.service",
	    "br.com.caixa.sidce.infraestructure.persistence.jpa",
	    "br.com.caixa.sidce.util.infraestructure.web",
	    "br.com.caixa.sidce.util.infraestructure.service",
	    "br.com.caixa.sidce.util.infraestructure.exception",
	    "br.com.caixa.sidce.interfaces.util",
	    "br.com.caixa.sidce.property",
	    "br.com.caixa.sidce.util.infraestructure.auditoria"
	})

@EnableAspectJAutoProxy(proxyTargetClass=true)
@EnableWebSecurity
@Import(value={ WebConfig.class, WebSecurityConfig.class })
@EnableJpaRepositories(basePackages = {"br.com.caixa.sidce.infraestructure.persistence.jpa"})
@EntityScan(basePackages = {"br.com.caixa.sidce.domain.model"})
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public AccessToken accessToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return ((KeycloakSecurityContext) ((KeycloakAuthenticationToken) request.getUserPrincipal()).getCredentials()).getToken();
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public Docket api() { 
    	return new Docket(DocumentationType.SWAGGER_2)
    	        .select()
    	        .apis(RequestHandlerSelectors.basePackage("br.com.caixa.sidce.interfaces.web"))
    	        .build()
    	        .apiInfo(metaData());

    	  }
    
    private ApiInfo metaData() {
        return new ApiInfoBuilder()
            .title("SIDCE")
            .description("\"Documentação API SIDCE\"")
            .version("1.0.0")
            .license("Caixa/Spread")
            .build();
      }
}
