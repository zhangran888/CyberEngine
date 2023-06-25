package com.NoteBook;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * @Author:zr
 * @CreateTime:2023-04-17
 * @Description：TODO
 */
public class ApiTest {
    public static void main(String[] args) {
        try {
            Mac hasher = Mac.getInstance("HmacSHA256");
            hasher.init(new SecretKeySpec("5921ba25ad974899bf8f2b48ef1cb77c".getBytes(), "HmacSHA256"));
            byte[] hash = hasher.doFinal("9936520aaa4745caa59c7cf61aeb4511".getBytes());
            // to lowercase hexits
            DatatypeConverter.printHexBinary(hash);
            // to base64
            String sign = DatatypeConverter.printBase64Binary(hash);
            System.out.println(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
