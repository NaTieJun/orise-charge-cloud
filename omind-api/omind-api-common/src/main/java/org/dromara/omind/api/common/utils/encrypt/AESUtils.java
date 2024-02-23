package org.dromara.omind.api.common.utils.encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtils {

    /**
     * AES128+CBC+PKCS5Padding 加密
     * @param secret 密钥
     * @param secretIv 密钥向量
     * @param data 待加密数据
     * @return Base64后的加密数据
     */
    public static String aes128CbcPKCS5Padding(String secret, String secretIv, String data)
    {
        try {
            byte[] keyBytes = secret.getBytes("utf-8");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(secretIv.getBytes("utf-8"));
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            // AES加密/CBC模式/PKCS5Padding填充方式（默认）
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
            byte[] encData = cipher.doFinal(data.getBytes("utf-8"));
            // base64
            Base64.Encoder encoder = Base64.getEncoder();
            String encodedText = encoder.encodeToString(encData);
            return encodedText;
        }
        catch (Exception ex){
            return "";
        }
    }

    public static String decrypt(String base64Encode, String secret_key, String vector_key) throws Exception {
        //实例化 Cipher 对象。加密算法/反馈模式/填充方案，解密与加密需要保持一致.
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //创建密钥。算法也要与实例化 Cipher 一致.
        SecretKey secretKey = new SecretKeySpec(secret_key.getBytes(), "AES");
        //有向量模式(CBC)需要传入 AlgorithmParameterSpec 算法参数规范参数.
        IvParameterSpec parameterSpec = new IvParameterSpec(vector_key.getBytes());
        //初始化 cipher。使用解密模式.
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        //将 Base64 编码的内容解码成字节数组(因为加密的时候，对密文使用了 Base64编码，所以这里需要先解码)
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] content = decoder.decode(base64Encode);
        //执行解密操作。返回解密后的字节数组，此时可以使用 String(byte bytes[]) 转成源字符串.
        byte[] decrypted = cipher.doFinal(content);
        return new String(decrypted);
    }

}
