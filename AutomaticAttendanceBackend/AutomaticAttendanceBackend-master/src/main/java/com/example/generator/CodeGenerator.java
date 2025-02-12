package com.example.generator;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class CodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "123456789";
    private static final int LENGTH = 6;
    private static final Random random = new SecureRandom();

    public static String generateCourseCode() {
        List<Character> chars = new ArrayList<>();
        for (char c : CHARACTERS.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars, random);

        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(chars.get(i));
        }
        return sb.toString();
    }

    public static Integer generateSessionCode() {
        List<Character> chars = new ArrayList<>();
        for (char c : NUMBERS.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars, random);

        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(chars.get(i));
        }
        return Integer.parseInt(sb.toString());
    }
}
