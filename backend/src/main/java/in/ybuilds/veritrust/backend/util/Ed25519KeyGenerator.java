package in.ybuilds.veritrust.backend.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class Ed25519KeyGenerator {

    public static void main(String[] args) throws Exception {

        KeyPairGenerator generator =
                KeyPairGenerator.getInstance("Ed25519");

        KeyPair keyPair = generator.generateKeyPair();

        String privateKey = Base64.getEncoder()
                .encodeToString(keyPair.getPrivate().getEncoded());

        String publicKey = Base64.getEncoder()
                .encodeToString(keyPair.getPublic().getEncoded());

        Path keysDirectory = Path.of("keys");

        Files.createDirectories(keysDirectory);

        Files.writeString(
                keysDirectory.resolve("institution-private-base64.txt"),
                privateKey
        );

        Files.writeString(
                keysDirectory.resolve("institution-public-base64.txt"),
                publicKey
        );

        System.out.println("Ed25519 key pair generated.");
        System.out.println("Private key length: " + privateKey.length());
        System.out.println("Public key length: " + publicKey.length());
    }
}
