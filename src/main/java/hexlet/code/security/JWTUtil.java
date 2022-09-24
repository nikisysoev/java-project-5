package hexlet.code.security;

import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Map;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.impl.TextCodec.BASE64;

@Component
public class JWTUtil {

    private final String secretKey;
    private final String issuer;
    private final Long expirationSec;
    private final Long clockSkewSec;
    private final Clock clock;

    public JWTUtil(@Value("${jwt.issuer}") final String issuer,
                     @Value("${jwt.expiration-sec}") final Long expirationSec,
                     @Value("${jwt.clock-skew-sec}") final Long clockSkewSec,
                     @Value("${jwt.secret}") final String secretKey) {

        this.secretKey = BASE64.encode(secretKey);
        this.issuer = issuer;
        this.expirationSec = expirationSec;
        //time offset
        this.clockSkewSec = clockSkewSec;
        this.clock = DefaultClock.INSTANCE;
    }

    public String generateToken(final String username) {
        return Jwts.builder()
                .signWith(HS256, secretKey)
                .setIssuer(issuer)
                .setIssuedAt(clock.now())
                .claim("username", username)
                //expires in 24 hours
                .setExpiration(new Date(System.currentTimeMillis() + expirationSec * 1000))
                .compact();
    }

    public Map<String, Object> validateToken(final String token) {
        return Jwts.parser()
                .requireIssuer(issuer)
                .setClock(clock)
                .setAllowedClockSkewSeconds(clockSkewSec)
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
