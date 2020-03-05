package com.intuit.quickfabric.commons.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.quickfabric.commons.constants.Roles;
import com.intuit.quickfabric.commons.domain.QuickFabricAuthenticationToken;
import com.intuit.quickfabric.commons.exceptions.QuickFabricUnauthenticatedException;
import com.intuit.quickfabric.commons.vo.ServiceType;
import com.intuit.quickfabric.commons.vo.ServiceVO;
import com.intuit.quickfabric.commons.vo.LoginRolesVO;

import io.jsonwebtoken.*;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTTokenProvider {

    private static final String USER_DETAILS = "userDetails";

    /**
     * THIS IS NOT A SECURE PRACTICE! For simplicity, we are storing a static key here. Ideally, in a
     * microservices environment, this key would be kept on a config-server.
     */
    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds = 3600000; // 1h

    @Autowired
    ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    String serviceName;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createTokenAuth(LoginRolesVO userDetails) {
        Claims claims = Jwts.claims().setSubject(userDetails.getEmailId());
        claims.put(USER_DETAILS, userDetails);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
    }

    //Method to get the Roles details from the token and not to do a DB trip every API call.
    public Authentication getAuthentication(String token) throws UsernameNotFoundException {
        QuickFabricAuthenticationToken quickFabricAuthenticationToken = null;

        if (token != null) {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            String userEmail = claims.getSubject();
            if (!StringUtils.isEmpty(userEmail)) {

                // Add new roles for application role i.e. EMR, Qualtrics
                ServiceType serviceType = ServiceType.valueOf(serviceName.toUpperCase());
                LoginRolesVO userDetails = objectMapper.convertValue(claims.get(USER_DETAILS), new TypeReference<LoginRolesVO>() {
                });

                ServiceVO service = null;
                List<SimpleGrantedAuthority> grantedAuthorityList = new ArrayList<>();
                if (userDetails != null) {
                    if (userDetails.isSuperAdmin()) {
                        grantedAuthorityList.add(new SimpleGrantedAuthority(Roles.SUPER_ADMIN));
                    }

                    if (userDetails.getServices() != null && !userDetails.getServices().isEmpty() && !userDetails.isSuperAdmin()) {
                        service = userDetails.getServices().stream().filter(x -> x.getServiceType() == serviceType).findFirst().orElse(null);
                        if (service != null) {
                            grantedAuthorityList = service
                                    .getRoles()
                                    .stream()
                                    .map(x -> x.getName()
                                            .toLowerCase())
                                    .distinct()
                                    .map(x -> new SimpleGrantedAuthority(x))
                                    .collect(Collectors.toList());
                        }
                    }

                    quickFabricAuthenticationToken = new QuickFabricAuthenticationToken(userDetails.getEmailId(), userDetails.getFirstName(),
                            userDetails.getLastName(), userDetails.isSuperAdmin(), grantedAuthorityList);
                    quickFabricAuthenticationToken.setDetails(service);
                }
            }
        }

        return quickFabricAuthenticationToken;
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Claims getClaims(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            throw new QuickFabricUnauthenticatedException("Expired or invalid JWT token");
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new QuickFabricUnauthenticatedException("Expired or invalid JWT token");
        }
    }
}
