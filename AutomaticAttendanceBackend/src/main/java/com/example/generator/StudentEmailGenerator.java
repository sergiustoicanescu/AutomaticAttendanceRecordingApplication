package com.example.generator;

import com.example.error.ValidationErrorException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentEmailGenerator {
    public static List<String> extractEmails(String text) {
        Set<String> emailSet = new HashSet<>();

        String emailRegex = "\\b[A-Za-z0-9._%+-]+@(e-uvt\\.ro|gmail\\.com)\\b";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            emailSet.add(matcher.group());
        }

        if(emailSet.isEmpty()) {
            throw new ValidationErrorException("No student emails found.");
        }

        return new ArrayList<>(emailSet);
    }
}
