package org.example.backendmpp;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.JwtSigner;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {
        private static final String SECRET_KEY = "6A90D0B6F131A844A26772665F5D342D1F0A8C6280DF1E5A7D5A7A2184A68DF4";

        public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
            // Extract a specific claim from the JWT
            final Claims claims = extractAllClaims(jwt);
            return claimsResolver.apply(claims);
        }

        public String extractUsername(String jwt) {
            // Extract the username from the JWT
            return extractClaim(jwt, Claims::getSubject);
        }

        public String generateToken(
                Map<String, Objects> extraClaims,
                UserDetails userDetails
        ){
            return Jwts.builder().setClaims(extraClaims)
                    .setSubject(userDetails.getUsername()).setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)).signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
        }

        public String generateToken(UserDetails userDetails) {
            // Generate a JWT token
            return generateToken(Map.of(), userDetails);
        }

        private Claims extractAllClaims(String jwt) {
            // Extract all claims from the JWT
            return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(jwt).getBody();
        }

        public boolean isTokenValid(String jwt, UserDetails userDetails) {
            // Check if the JWT is valid
            final String username = extractUsername(jwt);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
        }

    private boolean isTokenExpired(String jwt) {
        // Check if the JWT is expired
        return extractExpiration(jwt).before(new Date());
    }

    private Date extractExpiration(String jwt) {
        // Extract the expiration date from the JWT
        return extractClaim(jwt, Claims::getExpiration);
        }

    private Key getSignInKey() {
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            return Keys.hmacShaKeyFor(keyBytes);
        }
}
