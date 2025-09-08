package ru.lidzhiev.bankcards.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.lidzhiev.bankcards.entity.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Сервис для работы с JWT-токенами.
 * Предоставляет методы для генерации, проверки и извлечения информации из токенов.
 */
@Service
@Slf4j
public class JwtService {
    @Value("${app.jwt.secret}")
    private String jwtSigningKey;
    @Value("${app.jwt.expiration.hours}")
    private int tokenExpirationHours;
    public static long millisecondsInHour = 60 * 60 * 1000;

    /**
     * Извлекает имя пользователя из JWT-токена.
     *
     * @param token токен для анализа.
     * @return имя пользователя из токена.
     */
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Генерирует JWT-токен для указанного пользователя.
     *
     * @param userDetails объект пользователя с подробностями.
     * @return созданный JWT-токен.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("username", customUserDetails.getUsername());
            claims.put("userRole", customUserDetails.getUserRole());
        }
        return generateToken(claims, userDetails);
    }

    /**
     * Проверяет действительность JWT-токена для указанного пользователя.
     *
     * @param token        токен для проверки.
     * @param userDetails  объект пользователя с подробностями.
     * @return true, если токен действителен, иначе false.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Извлекает значение поля из JWT-токена.
     *
     * @param token              токен для анализа.
     * @param claimsResolvers    резольвер полей из JWT-клаимов.
     * @param <T>                тип возвращаемого значения.
     * @return извлеченное значение.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * Генерация JWT-токена с дополнительными утверждениями.
     *
     * @param extraClaims        дополнительные утверждения для токена.
     * @param userDetails        объект пользователя с подробностями.
     * @return сгенерированный JWT-токен.
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().claims(extraClaims).subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenExpirationHours * millisecondsInHour)) //24 hours expiration
                .signWith(getSigningKey()).compact();
    }

    /**
     * Проверяет, истек ли срок действия токена.
     *
     * @param token токен для проверки.
     * @return true, если токен просрочен, иначе false.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлекает дату истечения срока действия из токена.
     *
     * @param token токен для анализа.
     * @return дата истечения срока действия токена.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлекает все claims из JWT-токена.
     *
     * @param token токен для анализа.
     * @return объекты утверждений (claims).
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    /**
     * Возвращает ключ подписи для JWT-токенов.
     *
     * @return секретный ключ подписи.
     */    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
