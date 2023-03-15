package ru.nsu.trubitsyna.manager.utils;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Alphabet {
    private static final String LATIN_ALPHABET_WITH_NUMS = "abcdefghijklmnopqrstuvwxyz1234567890";

    public static List<String> asList() {
        return LATIN_ALPHABET_WITH_NUMS.chars().mapToObj(c -> String.valueOf((char)c)).toList();
    }

}
