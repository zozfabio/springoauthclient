package br.com.example.springoauthclient;

import br.com.example.springoauthclient.oauth.OAuth2LogoutSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.Resource;
import java.util.Arrays;

@Configuration
@EnableOAuth2Client
class OAuthClientConfiguration {

    @Resource
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private AccessTokenRequest accessTokenRequest;

    @Bean
    public OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails() {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();

        details.setId("spring-resource-server");
        details.setClientId("client");
        details.setClientSecret("123456");
        details.setAccessTokenUri("http://authorization.example.com:18080/oauth/token");
        details.setUserAuthorizationUri("http://authorization.example.com:18080/oauth/authorize");
        details.setScope(Arrays.asList("get", "list"));

        return details;
    }

    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
    public OAuth2RestTemplate oAuth2RestTemplate(OAuth2ProtectedResourceDetails resourceDetails) {
        return new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext(accessTokenRequest));
    }

    @Bean
    public ResourceServerTokenServices tokenServices() {
        RemoteTokenServices tokenServices = new RemoteTokenServices();

        tokenServices.setClientId("client");
        tokenServices.setClientSecret("123456");
        tokenServices.setCheckTokenEndpointUrl("http://authorization.example.com:18080/oauth/check_token");

        return tokenServices;
    }

    @Bean
    public OAuth2ClientAuthenticationProcessingFilter authenticationProcessingFilter(OAuth2RestOperations restTemplate, ResourceServerTokenServices tokenServices) {
        OAuth2ClientAuthenticationProcessingFilter authenticationProcessingFilter = new OAuth2ClientAuthenticationProcessingFilter("/login");

        authenticationProcessingFilter.setRestTemplate(restTemplate);
        authenticationProcessingFilter.setTokenServices(tokenServices);

        return authenticationProcessingFilter;
    }

    @Bean
    public OAuth2WebSecurityExpressionHandler webSecurityExpressionHandler() {
        return new OAuth2WebSecurityExpressionHandler();
    }

    @Bean
    public OAuth2LogoutSuccessHandler oAuth2LogoutSuccessHandler() {
        OAuth2LogoutSuccessHandler logoutSuccessHandler = new OAuth2LogoutSuccessHandler();

        logoutSuccessHandler.setDefaultLogoutSuccessUri("/");
        logoutSuccessHandler.setLogoutUri(ServletUriComponentsBuilder
                .fromUriString("http://authorization.example.com:18080/logout")
                .build()
                .toUri());

        return logoutSuccessHandler;
    }
}
