
package in.ybuilds.veritrust.backend.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class Ed25519Service {

    public String sign(
            String payload,
            PrivateKey privateKey
    ) throws Exception {

        Signature signature = Signature.getInstance("Ed25519");

        signature.initSign(privateKey);

        signature.update(
                payload.getBytes(StandardCharsets.UTF_8)
        );

        byte[] signedBytes = signature.sign();

        return Base64.getEncoder()
                .encodeToString(signedBytes);
    }

    public boolean verify(
            String payload,
            String signatureBase64,
            java.security.PublicKey publicKey
    ) throws Exception {

        Signature signature = Signature.getInstance("Ed25519");

        signature.initVerify(publicKey);

        signature.update(
                payload.getBytes(
                        java.nio.charset.StandardCharsets.UTF_8
                )
        );

        byte[] signatureBytes =
                java.util.Base64.getDecoder()
                        .decode(signatureBase64);

        return signature.verify(signatureBytes);
    }

    public boolean verifyKeyPair(
            String testPayload,
            java.security.PrivateKey privateKey,
            java.security.PublicKey publicKey
    ) throws Exception {

        String generatedSignature = sign(
                testPayload,
                privateKey
        );

        return verify(
                testPayload,
                generatedSignature,
                publicKey
        );
    }

    public PublicKey decodePublicKey(String publicKeyBase64)
            throws Exception {

        byte[] keyBytes = Base64.getDecoder()
                .decode(publicKeyBase64);

        X509EncodedKeySpec keySpec =
                new X509EncodedKeySpec(keyBytes);

        KeyFactory keyFactory =
                KeyFactory.getInstance("Ed25519");

        return keyFactory.generatePublic(keySpec);
    }
}
