package imangazaliev.scripto.utils;

import java.util.Random;

public class StringUtils {


    public static String randomString(int stringLength) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder randomStringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < stringLength; i++) {
            char c = chars[random.nextInt(chars.length)];
            randomStringBuilder.append(c);
        }
        return randomStringBuilder.toString();
    }

    public static String randomNumericString(int stringLength) {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        for (int i = 0; i < stringLength; i++){
            randomStringBuilder.append(generator.nextInt(9));
        }
        return randomStringBuilder.toString();
    }

}
