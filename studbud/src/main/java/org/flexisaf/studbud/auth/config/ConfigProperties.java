package org.flexisaf.studbud.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


@ConfigurationProperties(prefix = "app.security")
public record ConfigProperties(RSAKeys rsaKeys) {
    record RSAKeys(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
    }
}
