package com.example.onharu.global.util;

public class RandomUtil {

    public static int generateCode() {
        return (int) ((Math.random() * 9000) + 1000);
    }
}
