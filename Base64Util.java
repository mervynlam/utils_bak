

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @Title: Base64Utils
 * @Description:
 * @date: 2021/8/25 15:15
 */
public class Base64Util {
    /**
     * BASE64编码器
     */
    private final static Base64.Encoder ENCODER = Base64.getEncoder();
    /**
     * BASE64解码器
     */
    private final static Base64.Decoder DECODER = Base64.getDecoder();

    /**
     * 对JSON格式的Map数据进行Base 64加密
     * @param map Map数据
     * @param confound 混淆字符串（应与Base64特点一致，长度为4个倍数，以加强混淆程度）
     * @param iterate 迭代次数
     * @return string JSON加密后的字符串
     */
    public static String encodeJson(Map<String, Object> map, String confound, int iterate){
        String json = JSON.toJSONString(map);
        return encode(json, confound, iterate);
    }

    /**
     * 对JSON格式的数据进行Base 64加密
     * @param obj 源数据
     * @param confound 混淆字符串（应与Base64特点一致，长度为4个倍数，以加强混淆程度）
     * @return string JSON加密后的字符串
     */
    public static String encodeJson(Object obj, String confound){
        int iterate = 2;
        String json = JSON.toJSONString(obj);
        return encode(json, confound, iterate);
    }

    /**
     * 对JSON格式的Map数据进行Base 64解密
     * @param str Map数据的JSON字符串
     * @param confound 混淆字符串（应与Base64特点一致，长度为4个倍数，以加强混淆程度）
     * @param iterate 迭代次数
     * @return string JSON加密后的字符串
     */
    public static Map<String, Object> decodeJson(String str, String confound, int iterate){
        String decoder = decode(str, confound, iterate);
        JSONObject json = JSONObject.parseObject(decoder);
        return JSONObject.toJavaObject(json, Map.class);
    }

    /**
     * 对JSON格式的Map数据进行Base 64解密
     * @param str Map数据的JSON字符串
     * @param confound 混淆字符串（应与Base64特点一致，长度为4个倍数，以加强混淆程度）
     * @return string JSON加密后的字符串
     */
    public static Map<String, Object> decodeJson(String str, String confound){
        int iterate = 2;
        String decoder = decode(str, confound, iterate);
        JSONObject json = JSONObject.parseObject(decoder);
        return JSONObject.toJavaObject(json, Map.class);
    }

    /**
     * 进行Base64加密
     * @param str 源字符串
     * @param confound 混淆字符串（应与Base64特点一致，长度为4个倍数，以加强混淆程度）
     * @param iterate 迭代次数
     * @return 加密后字符串
     */
    public static String encode(String str, String confound, int iterate){
        Assert.isTrue(iterate > 0, "迭代次数错误");
        Assert.notNull(str, "源字符串缺失");
        String result = str;
        while (iterate-- > 0){
            result = Base64Util.encode(confound + result + confound).replaceAll("\r\n","");
        }
        return result;
    }

    /**
     * 进行Base64加密
     * @param str 源字符串
     * @param confound 混淆字符串（应与Base64特点一致，长度为4个倍数，以加强混淆程度）
     * @return 加密后字符串
     */
    public static String encode(String str, String confound){
        Assert.notNull(str, "源字符串缺失");
        int iterate = 2;
        String result = str;
        while (iterate-- > 0){
            result = Base64Util.encode(confound + result + confound).replaceAll("\r\n","");
        }
        return result;
    }

    /**
     * 进行Base64解密
     * @param str 源字符串
     * @param confound 混淆字符串
     * @param iterate 迭代次数
     * @return 解密后字符串
     */
    public static String decode(String str, String confound, int iterate){
        Assert.isTrue(iterate > 0, "迭代次数错误");
        Assert.notNull(str, "源字符串缺失");
        String result = str;
        if(Base64Util.isBase64(str)){
            int pos;
            while (iterate-- > 0){
                if(confound.length() > 0){
                    result = Base64Util.decode(result);
                    if(result.length() > confound.length()){
                        result = result.substring(confound.length());
                    }
                    pos = result.lastIndexOf(confound);
                    result = pos == -1 ? result : result.substring(0, pos);
                } else {
                    result = Base64Util.decode(result);
                }
            }
        }
        return result;
    }

    /**
     * 进行Base64解密
     * @param str 源字符串
     * @param confound 混淆字符串
     * @return 解密后字符串
     */
    public static String decode(String str, String confound){
        Assert.notNull(str, "源字符串缺失");
        String result = str;
        int iterate = 2;
        if(Base64Util.isBase64(str)){
            int pos;
            while (iterate-- > 0){
                if(confound.length() > 0){
                    result = Base64Util.decode(result);
                    if(result.length() > confound.length()){
                        result = result.substring(confound.length());
                    }
                    pos = result.lastIndexOf(confound);
                    result = pos == -1 ? result : result.substring(0, pos);
                } else {
                    result = Base64Util.decode(result);
                }
            }
        }
        return result;
    }

    /**
     * 进行一次Base64加密
     * @param str 源字符串
     * @return 加密后字符串
     */
    public static String encode(String str){
        Assert.notNull(str, "源字符串缺失");
        return ENCODER.encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 进行一次Base64解密
     * @param str 源字符串
     * @return 解密后字符串
     */
    public static String decode(String str){
        Assert.notNull(str, "源字符串缺失");
        try {
            str = new String(DECODER.decode(str), StandardCharsets.UTF_8);
        } catch (Exception ignored) {}
        return str;
    }

    /**
     * 私有函数
     * 判断源字符串是否经过为Base64加密
     * @param str 源字符串
     * @return 是否经过为Base64加密
     */
    private static boolean isBase64(String str){
        String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        return Pattern.matches(base64Pattern, str);
    }

}
