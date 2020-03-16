package com.example.demo.BusinessLayer;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class AccessCodeGenerator {
    private static Set<String> accessCodes = new HashSet<>();

    public static String getAccessCode() {
        int codeLen = 10;
        String accessCode = getAccessCode(codeLen);
        accessCodes.add(accessCode);
        return accessCode;
    }

    private static String getAccessCode(int targetLen) {
        String generatedString = "";
        Random random = new Random();
        do {
            generatedString = random.ints('0', 'z' + 1)
                    .filter(AccessCodeGenerator::isNumberOrLowercase)
                    .limit(targetLen)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        } while (accessCodes.contains(generatedString));
        return generatedString;
    }

    private static boolean isNumberOrLowercase(int i) {
        return ('0' <= i && i <= '9') || ('a' <= i && i <= 'z');
    }
}
