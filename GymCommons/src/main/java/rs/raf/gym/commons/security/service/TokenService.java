/*
 * Copyright (C) 2024. Lazar Dobrota and Nemanja Radovanovic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rs.raf.gym.commons.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.raf.gym.commons.configuration.ServiceConfiguration;

import javax.crypto.SecretKey;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService{

    private SecretKey secretKey;

    private final ServiceConfiguration configuration;


    @PostConstruct
    private void init() {
        byte[] secretKeyBytes = configuration.secret.getBytes();
        secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    @Override
    public String encrypt(Map<String, Object> payload) {
        return Jwts.builder().claims(payload).signWith(secretKey).compact();
    }

    @Override
    public Claims decipherToken(String token) {
        Claims payload;
        try {
            payload = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        }
        catch (Exception e) {
            return null;
        }

        return payload;
    }
}
