package rocks.ethanol.ethanolmod.auth.key;

import rocks.ethanol.ethanolmod.utils.HashUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public record AuthKeyPair(String name, KeyPair keyPair, byte[] hash) {

    private static final KeyFactory RSA_KEY_FACTORY;

    static {
        try {
            RSA_KEY_FACTORY = KeyFactory.getInstance("RSA");
        } catch (final NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static AuthKeyPair fromFile(final Path keyFile) throws Exception {
        final KeyPair pair = AuthKeyPair.read(ByteBuffer.wrap(Files.readAllBytes(keyFile)));
        final String name = keyFile.getFileName().toString();
        final byte[] hash = HashUtil.hashSha256(pair.getPublic().getEncoded());
        return new AuthKeyPair(name, pair, hash);
    }

    private static KeyPair read(final ByteBuffer buffer) throws Exception {
        final byte[] publicKey = new byte[buffer.getShort()];
        buffer.get(publicKey);
        final byte[] privateKey = new byte[buffer.getShort()];
        buffer.get(privateKey);

        if (buffer.hasRemaining()) {
            throw new IOException("Trailing data at end of file!");
        }

        try {
            return new KeyPair(AuthKeyPair.RSA_KEY_FACTORY.generatePublic(new X509EncodedKeySpec(publicKey)), AuthKeyPair.RSA_KEY_FACTORY.generatePrivate(new PKCS8EncodedKeySpec(privateKey)));
        } catch (final Exception exception) {
            throw new IOException(exception);
        }
    }

}
