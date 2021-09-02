

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Title: Md5EncryptionUtils
 * @Description:
 * @date: 2021/8/24 15:37
 */
public class Md5EncryptionUtils {

    /**
     * @Title: getMD5
     * @Description: 获取MD5字符串
     * @date: 2021/8/24 15:37
     */
    public static String getMD5(String content) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(content.getBytes());
        return getHashString(digest);
    }

    /**
     * @Title: getMD5WithSalt
     * @Description: 获取加盐的MD5字符串
     * @date: 2021/8/24 15:38
     */
    public static String getMD5WithSalt(String content, final String SALT) throws NoSuchAlgorithmException {
        return getMD5(getMD5(content) + getMD5(SALT));
    }

    private static String getHashString(MessageDigest digest) {
        StringBuilder builder = new StringBuilder();
        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }
}
