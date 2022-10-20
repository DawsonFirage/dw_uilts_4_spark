package com.dwsn.bigdata.test;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class DecodeTest {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String raw = "%E4%B8%8A%E6%8A%A5";
        System.out.println(URLDecoder.decode(raw, "utf-8"));

    }
}
