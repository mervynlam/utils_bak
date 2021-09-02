import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Scanner;

/**
 * @Title: CodeUtils
 * @Description: 生成机器码工具类
 * @date: 2021/8/24 14:19
 */
@Slf4j
public class CodeUtils {

    private final String SALT = "salt";

    /**
     * @Title: getMachineCode
     * @Description: 生成机器码
     * @date: 2021/8/31 10:19
     */
    public static String getMachineCode() throws IOException, NoSuchAlgorithmException {
        log.info("生成机器码");
        StringBuilder sb = new StringBuilder();

        //获取 CPU 序列号
        String cpuSerial = getCpuSerial();
        sb.append(cpuSerial);
        //获取系统版本
        Properties props = System.getProperties();
        String osVersion = props.getProperty("os.version");
        sb.append(osVersion);

        String code = null;
        try {
            code = Md5EncryptionUtils.getMD5WithSalt(sb.toString(), SALT);
        } catch (NoSuchAlgorithmException e) {
            log.error("生成机器码失败");
            throw e;
        }
        return code.toUpperCase();
    }

    private static String getSplitString(String str, String split, int length) {
        int len = str.length();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i % length == 0 && i > 0) {
                temp.append(split);
            }
            temp.append(str.charAt(i));
        }
        String[] attrs = temp.toString().split(split);
        StringBuilder finalMachineCode = new StringBuilder();
        for (String attr : attrs) {
            if (attr.length() == length) {
                finalMachineCode.append(attr).append(split);
            }
        }
        String result = finalMachineCode.toString().substring(0,
                finalMachineCode.toString().length() - 1);
        return result;
    }


    /**
     * @Title: getCpuSerial
     * @Description: 获取CPU序列号
     * @date: 2021/8/23 14:21
     */
    public static String getCpuSerial() throws IOException {
        log.info("获取CPU序列号");
        // linux，windows命令
        String[] linux = {"dmidecode", "-t", "processor", "|", "grep", "'ID'"};
        String[] windows = {"wmic", "cpu", "get", "ProcessorId"};

        Scanner sc;
        // 获取系统信息
        try {
            String property = System.getProperty("os.name");
            Process process = Runtime.getRuntime().exec(property.contains("Window") ? windows : linux);
            process.getOutputStream().close();
            sc = new Scanner(process.getInputStream(), "utf-8");
            sc.next();
        } catch (IOException e) {
            log.error("获取CPU序列号失败");
            throw e;
        }
        return sc.next().toUpperCase();
    }
}
