package imangazaliev.scripto.utils;

import java.util.Random;

public class StringUtils {


    public static String randomString(int stringLength) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder randomStringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            randomStringBuilder.append(c);
        }
        return randomStringBuilder.toString();
    }

    public static String randomStringNumeric(int stringLength) {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(stringLength);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

}
