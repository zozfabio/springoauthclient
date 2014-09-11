package br.com.example.springoauthclient;

import br.com.example.springoauthclient.oauth.OAuth2LogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2WebSecurityExpressionHandler webSecurityExpressionHandler;

    @Autowired
    private LoginUrlAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private OAuth2ClientContextFilter clientContextFilter;

    @Autowired
    private OAuth2ClientAuthenticationProcessingFilter authenticationProcessingFilter;

    @Autowired
    private OAuth2LogoutSuccessHandler logoutSuccessHandler;

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security
            .csrf()
                .disable()
            .authorizeRequests()
                .expressionHandler(webSecurityExpressionHandler)
                .antMatchers("/login").anonymous()

                .antMatchers("/get").access("#oauth2.hasScope('get')")
                .antMatchers("/list").access("#oauth2.hasScope('list')")

                .antMatchers("/", "/webjars/**").permitAll()

                .anyRequest().denyAll()
            .and()
            .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
            .and()
            .logout()
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .logoutSuccessHandler(logoutSuccessHandler)
            .and()
            .addFilterBefore(clientContextFilter, AbstractPreAuthenticatedProcessingFilter.class)
            .addFilterAfter(authenticationProcessingFilter, OAuth2ClientContextFilter.class);
    }

    @Bean
    public LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint() {
        return new LoginUrlAuthenticationEntryPoint("/login");
    }
}