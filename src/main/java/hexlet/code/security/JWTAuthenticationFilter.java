package hexlet.code.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LoginDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JWTUtil jwtUtil;

    public JWTAuthenticationFilter(final AuthenticationManager authenticationManager,
                                   final RequestMatcher loginRequest,
                                   final JWTUtil jwtUtil) {

        super(authenticationManager);
        //Sets the URL that determines if authentication is required
        setRequiresAuthenticationRequestMatcher(loginRequest);
        this.jwtUtil = jwtUtil;
    }

    //log in
    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
            throws AuthenticationException {

        //parse LoginDto
        final ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginDto;
        try {
            final String json = request.getReader()
                    .lines()
                    .collect(Collectors.joining());
            loginDto = objectMapper.readValue(json, LoginDto.class);
        } catch (IOException e) {
            throw new BadCredentialsException(e.getMessage());
        }

        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), //email is used as username
                loginDto.getPassword()
        );
        setDetails(request, authenticationToken);
        //try to log in
        return getAuthenticationManager().authenticate(authenticationToken);
    }

    //in success case
    @Override
    protected void successfulAuthentication(final HttpServletRequest request,
                                            final HttpServletResponse response,
                                            final FilterChain chain,
                                            final Authentication authResult) throws IOException {

        final UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        final String jwtToken = jwtUtil.generateToken(userDetails.getUsername()); //only username without password

        //return jwtToken to client
        response.getWriter().print(jwtToken);
    }
}
