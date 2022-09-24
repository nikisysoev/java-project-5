package hexlet.code.security;

import hexlet.code.service.implementation.UsersDetailsServiceImpl;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final RequestMatcher publicUrls;
    private final UsersDetailsServiceImpl usersDetailsServiceImpl;

    public JWTAuthorizationFilter(final JWTUtil jwtUtil,
                                  final RequestMatcher publicUrls,
                                  final UsersDetailsServiceImpl usersDetailsServiceImpl) {
        this.jwtUtil = jwtUtil;
        this.publicUrls = publicUrls;
        this.usersDetailsServiceImpl = usersDetailsServiceImpl;
    }

    //urls without authorization checks
    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return publicUrls.matches(request);
    }

    //check jwtToken existence and provide authorities to client
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        //check jwtToken
        final String username = Optional.ofNullable(request.getHeader(AUTHORIZATION))
                .map(header -> header.replaceFirst("Bearer", ""))
                .map(String::trim)
                .map(jwtUtil::validateToken)
                .map(claims -> claims.get("username"))
                .map(Object::toString)
                .orElseThrow();

        final UserDetails userDetails =
                usersDetailsServiceImpl.loadUserByUsername(username);

        //provide authorities
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(), null, userDetails.getAuthorities());

        //update authentication token
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //next filters
        filterChain.doFilter(request, response);
    }
}
