package rocks.ethanol.ethanolmod.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    private static final MessageDigest SHA_256;
    public static final int SHA_256_SIZE = 256 / 8;

    static {
        try {
            SHA_256 = MessageDigest.getInstance("SHA-256");
        } catch (final NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static byte[] hashSha256(final byte[] input) {
        return HashUtil.SHA_256.digest(input);
    }

}
