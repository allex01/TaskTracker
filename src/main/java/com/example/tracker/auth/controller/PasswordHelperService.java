package com.example.tracker.auth.controller;

import java.util.Random;

public class PasswordHelperService {
    public static String generatePassword(String userId) {
        final int passwordLength = 9;

        // Набор спецсимволов
        final char[] specialSymbols = new char[] {
            '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*'
        };

        // Набор русских букв
        final char[] russianLowercase = new char[] {
            'a','б','в','г','д','е','ё','ж','з','и','й','к','л','м','н','о','п','р','с','т','у','ф','х','ц','ч','ш','щ','ъ','ы','ь','э','ю','я'
        };

        Random random = new Random();

        int n = userId == null ? 0 : userId.length();
        int q = n % 5;               // Q = N mod 5
        int numSpecial = q + 1;      // b1..b(Q+1) — спецсимволы

        StringBuilder password = new StringBuilder(passwordLength);

        for (int i = 0; i < numSpecial && password.length() < passwordLength - 1; i++) {
            char c = specialSymbols[random.nextInt(specialSymbols.length)];
            password.append(c);
        }

        while (password.length() < passwordLength - 1) {
            char c = russianLowercase[random.nextInt(russianLowercase.length)];
            password.append(c);
        }
        
        char digit = (char) ('0' + random.nextInt(10));
        password.append(digit);

        return password.toString();
    }
}
