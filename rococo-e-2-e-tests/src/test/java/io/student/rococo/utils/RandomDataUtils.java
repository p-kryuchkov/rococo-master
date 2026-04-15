package io.student.rococo.utils;

import com.github.javafaker.Faker;

import java.util.Locale;

public class RandomDataUtils {
    private static final Faker faker = new Faker(new Locale("ru"));

    public static String randomUsername() {
        return faker.name().username();
    }

    public static String randomPassword() {
        return faker.internet().password(8,12);
    }


    public static String randomName() {
        return faker.name().firstName();
    }

    public static String randomSurname() {
        return faker.name().lastName();
    }

    public static String randomAirport() {
        return faker.aviation().airport();
    }

    public static String randomSentence(int wordsCount) {
        return faker.lorem().sentence(wordsCount);
    }
}
