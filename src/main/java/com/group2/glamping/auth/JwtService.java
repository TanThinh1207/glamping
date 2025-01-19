package com.group2.glamping.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
// Binh
public class JwtService {

    private static final String SECRET_KEY = "SNMBP1D7/V2QFUC4Twr1cZgictGITTT6FSJrA3Y6o2nljOsSgrAUNrvQIFgtDKvwkTkemjEPMTa9pRZvsDhpQjuSJc9I4dNMQ/Y8O6DZdE7pWH9ISEQKpAxwU4cQ4sqUVYEnzLEU8+GFMihfE/Z/jESlKpwcaPuT2ygcQwBTz3of2M0K3yEFQK9usaKKX9+C2ryu6F5Q6rclJUd7DlBxTdRRstqCumu3EEAVSwqzXEBdx73TV3cFAxh93YpzaFjCc6xTaKZR7Sb5irKVPXH0rW0c+8Zi+KVVxh5PdTDuDCVqwZn2O6bpDNFpG1yK4g1fcsDfH2+XWXLeMMdpSkUWTTPDW0tRF+jas/ss70kuXvI=\n" +
            "\n";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractClaims(token);
        return claimResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extractClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .claims(extractClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // token last for 24hours + 1000
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();

    }

    // validate if token is belong to userDetail
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
