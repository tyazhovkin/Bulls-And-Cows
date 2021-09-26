package org.hyperskill.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BullsCowsTest {
    private static ByteArrayOutputStream output;
    private static ByteArrayInputStream input;
    private static final PrintStream DEFAULT_STDOUT = System.out;
    private static final InputStream DEFAULT_STDIN = System.in;

    private void provideInput(String data) {
        input = new ByteArrayInputStream(data.getBytes());
        System.setIn(input);
    }

    private String getOutput() {
        return output.toString();
    }

    @BeforeEach
    public void setUpStreams() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    public void rollbackChangesToStdout() {
        System.setOut(DEFAULT_STDOUT);
    }

    @AfterEach
    public void rollbackChangesToStdin() {
        System.setIn(DEFAULT_STDIN);
    }

    @Test
    public void test1() {
        StringBuilder builder = new StringBuilder();
        builder.append("1\n");
        builder.append("10\n");
        for (int i = 0; i <= 9; i++) {
            builder.append(i);
            builder.append("\n");
        }
        provideInput(builder.toString());
        Main.main(new String[0]);
        checkAnswer(getOutput(), "The secret is prepared: * (0-9).");
    }

    @Test
    public void test2() {
        char[] dictionary = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z'};

        StringBuilder builder = new StringBuilder();
        builder.append("1\n");
        builder.append("36\n");

        for (int i = 0; i < 36; i++) {
            builder.append(dictionary[i]);
            builder.append("\n");
        }

        provideInput(builder.toString());
        Main.main(new String[0]);
        checkAnswer(getOutput(), "The secret is prepared: * (0-9, a-z).");
    }

    @Test
    public void test3() {
        char[] dictionary = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z'};

        StringBuilder builder = new StringBuilder();
        builder.append("1\n");
        builder.append("25\n");

        for (int i = 0; i < 25; i++) {
            builder.append(dictionary[i]);
            builder.append("\n");
        }

        provideInput(builder.toString());
        Main.main(new String[0]);
        checkAnswer(getOutput(), "The secret is prepared: * (0-9, a-o).");
    }

    @Test
    public void test4() {
        provideInput("10\n9");
        Main.main(new String[0]);
        checkError(getOutput(), "Error: it's not possible to generate a code with a length of 10 with 9 unique symbols.");
    }

    @Test
    public void test5() {
        provideInput("9\n37");
        Main.main(new String[0]);
        checkError(getOutput(), "Error: maximum number of possible symbols in the code is 36 (0-9, a-z).");
    }

    @Test
    public void test6() {
        provideInput("0\n0");
        Main.main(new String[0]);
        checkError(getOutput(), "Error: can't generate a secret number with a length of 0 because there aren't enough unique digits.");
    }

    @Test
    public void test7() {
        provideInput("abcdefg 1 -6");
        Main.main(new String[0]);
        checkError(getOutput(), "Error: abcdefg 1 -6 isn't a valid number.");
    }

    private void checkError(String reply, String clue) {
        String[] lines = reply.split("\\n");
        for (String line : lines
        ) {
            if (line.contains("Error:")) {
                Assertions.assertEquals(line.trim(), clue);
                return;
            }
        }
        Assertions.fail("Expected - " + clue);
    }

    private void checkAnswer(String reply, String clue) {
        String[] lines = reply.split("\\n");
        int gotAnswer = 0;

        if (lines.length <= 1) {
            Assertions.fail("Looks like you didn't print anything!");
            return;
        }

        for (String line : lines
        ) {
            if (line.contains("The secret is prepared:")) {
                Assertions.assertEquals(line.trim(), clue);
            }
            if (line.contains("Grade")) {
                int[] result = getNumOfBullsAndCows(line);
                if (result[0] == 1) {
                    gotAnswer++; // if got a bull, count for an amount of answers
                }
            }
        }

        if (gotAnswer != 1) {
            Assertions.fail("The game has no answer or more than one. ");
        }
    }

    //get number of bulls and cows from user program's output
    private int[] getNumOfBullsAndCows(String userString) {
        Matcher nonePattern = Pattern.compile("\\b[nN]one\\b").matcher(userString);
        Matcher cowsPattern = Pattern.compile("\\b\\d [cC]ow").matcher(userString);
        Matcher bullsPattern = Pattern.compile("\\b\\d [bB]ull").matcher(userString);
        Pattern oneNumPattern = Pattern.compile("\\d");

        if (nonePattern.find()) {
            return new int[]{0, 0};
        }

        int[] ans = {0, 0};
        boolean found = false;

        if (bullsPattern.find()) {
            String temp = bullsPattern.group();
            Matcher oneNumBulls = oneNumPattern.matcher(temp);
            oneNumBulls.find();
            ans[0] = Integer.parseInt(oneNumBulls.group());
            found = true;
        }

        if (cowsPattern.find()) {
            String temp = cowsPattern.group();
            Matcher oneNumCows = oneNumPattern.matcher(temp);
            oneNumCows.find();
            ans[1] = Integer.parseInt(oneNumCows.group());
            found = true;
        }

        if (!found) {
            Assertions.fail("Cannot find number of bulls or number of cows or None after the input.");
        }

        return ans;
    }
}