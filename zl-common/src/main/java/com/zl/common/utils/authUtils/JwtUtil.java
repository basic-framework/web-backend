package com.zl.common.utils.authUtils;

import com.zl.common.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.Map;


 /**
 * Jwt工具类
 * @Auther GuihaoLv
 */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class JwtUtil {

    @Autowired
    private JwtProperties jwtProperties;

    /**
    * 生成用于 JWT 签名和验证的密钥
    * @return
    */
    private Key getKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }



     /**
     * 生成JWT令牌
     * @param claims JWT第二部分负载 payload 中存储的内容
     * @return JWT Token
     */
    public  String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpireTime()))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析JWT令牌
     * @param token JWT Token
     * @return 存储在 Token 中的信息
     */
    public  Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }


}