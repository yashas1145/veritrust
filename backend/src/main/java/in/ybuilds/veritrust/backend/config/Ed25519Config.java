package in.ybuilds.veritrust.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class Ed25519Config {

    @Bean
    public PrivateKey institutionPrivateKey() throws Exception {

        String privateKeyBase64 =
                System.getenv("ED25519_PRIVATE_KEY");

        if (privateKeyBase64 == null || privateKeyBase64.isBlank()) {
            throw new IllegalStateException(
                    "ED25519_PRIVATE_KEY environment variable is missing"
            );
        }

        byte[] keyBytes =
                Base64.getDecoder().decode(privateKeyBase64);

        PKCS8EncodedKeySpec keySpec =
                new PKCS8EncodedKeySpec(keyBytes);

        KeyFactory keyFactory =
                KeyFactory.getInstance("Ed25519");

        return keyFactory.generatePrivate(keySpec);
    }

    @Bean
    public PublicKey institutionPublicKey() throws Exception {

        String publicKeyBase64 =
                System.getenv("ED25519_PUBLIC_KEY");

        if (publicKeyBase64 == null || publicKeyBase64.isBlank()) {
            throw new IllegalStateException(
                    "ED25519_PUBLIC_KEY environment variable is missing"
            );
        }

        byte[] keyBytes =
                Base64.getDecoder().decode(publicKeyBase64);

        X509EncodedKeySpec keySpec =
                new X509EncodedKeySpec(keyBytes);

        KeyFactory keyFactory =
                KeyFactory.getInstance("Ed25519");

        return keyFactory.generatePublic(keySpec);
    }

    @Bean
    public boolean validateEd25519KeyPair(
            PrivateKey institutionPrivateKey,
            PublicKey institutionPublicKey,
            in.ybuilds.veritrust.backend.service.Ed25519Service ed25519Service
    ) throws Exception {

        boolean valid = ed25519Service.verifyKeyPair(
                "VeriTrust Ed25519 key validation",
                institutionPrivateKey,
                institutionPublicKey
        );

        if (!valid) {
            throw new IllegalStateException(
                    "Ed25519 private and public keys do not match"
            );
        }

        return true;
    }
}
