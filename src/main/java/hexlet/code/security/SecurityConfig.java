package hexlet.code.security;

import hexlet.code.service.implementation.UsersDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String LOGIN = "/login";
    private final UsersDetailsServiceImpl usersDetailsServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final AntPathRequestMatcher loginRequest;
    private final OrRequestMatcher publicUrls;

    public SecurityConfig(@Value("${base-url}") final String baseUrl,
                          final UsersDetailsServiceImpl usersDetailsServiceImpl,
                          final PasswordEncoder passwordEncoder,
                          final JWTUtil jwtUtil) {

        // - POST('/api/login')
        this.loginRequest = new AntPathRequestMatcher(baseUrl + LOGIN, POST.toString());
        // - POST('/api/users')
        // - GET('/api/users')
        // - all urls without '/api' in the beginning
        this.publicUrls = new OrRequestMatcher(
                loginRequest,
                new AntPathRequestMatcher(baseUrl + USER_CONTROLLER_PATH, POST.toString()),
                new AntPathRequestMatcher(baseUrl + USER_CONTROLLER_PATH, GET.toString()),
                new NegatedRequestMatcher(new AntPathRequestMatcher(baseUrl + "/**"))
        );
        this.usersDetailsServiceImpl = usersDetailsServiceImpl;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(usersDetailsServiceImpl)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        //to authenticate
        final JWTAuthenticationFilter authenticationFilter = new JWTAuthenticationFilter(
                authenticationManagerBean(),
                loginRequest,
                jwtUtil
        );

        //to authorize
        final JWTAuthorizationFilter authorizationFilter = new JWTAuthorizationFilter(
                jwtUtil,
                publicUrls,
                usersDetailsServiceImpl
        );

        //permissions
        http.csrf().disable()
                .authorizeRequests()
                .requestMatchers(publicUrls).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(authenticationFilter)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable();
    }
}
