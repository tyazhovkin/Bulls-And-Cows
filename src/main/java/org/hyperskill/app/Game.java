package org.hyperskill.app;

import java.io.InputStream;
import java.util.Random;
import java.util.Scanner;

public class Game {
    public static final int MAX_LENGTH = 36;
    private Scanner scanner;
    private final Random random = new Random();
    private String secretCode;
    private int numberOfTurns;
    private boolean victory;
    private boolean finishGame;
    private char[] chars;
    private int lengthSecretCode;
    private int numberOfPossibleSymbols;
    private String input;

    public boolean isVictory() {
        return victory;
    }

    public boolean isFinishGame() {
        return finishGame;
    }

    public Game() {
        generateSymbols();
        setInputStream(System.in);
    }

    public void setInputStream(InputStream inputStream) {
        scanner = new Scanner(inputStream);
    }

    public void Start() {
        victory = false;
        generateSecretCode();
    }

    public void Stop() {
        if (isVictory()) {
            System.out.println("Congratulations! You guessed the secret code.");
        }
        finishGame = true;
    }

    public void nextTurn() {
        numberOfTurns++;
        System.out.printf("Turn %d:\n", numberOfTurns);
        input = scanner.next();
        gradeATurn(input);
        if (secretCode.equals(input)) {
            victory = true;
            Stop();
        }
    }

    private void generateSymbols() {
        chars = new char[MAX_LENGTH];
        for (int i = 0; i < 10; i++) {
            chars[i] = (char) ('0' + i);
        }
        int j = 10;
        for (int i = 0; i < 26; i++) {
            chars[j] = (char) ('a' + i);
            j++;
        }
    }

    private void shuffleSymbols() {
        for (int i = numberOfPossibleSymbols - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char a = chars[index];
            chars[index] = chars[i];
            chars[i] = a;
        }
    }

    private boolean isCorrectInputNumber(String input) {
        if (!input.matches("\\d+")) {
            System.out.printf("Error: %s isn't a valid number.", input);
            Stop();
        }
        return !isFinishGame();
    }

    private void checkCorrectInputBeforeGenerate() {
        if (lengthSecretCode > MAX_LENGTH || lengthSecretCode <= 0) {
            System.out.printf("Error: can't generate a secret number with a length of %d because" +
                    " there aren't enough unique digits.", lengthSecretCode);
            Stop();
        } else if (numberOfPossibleSymbols > MAX_LENGTH || numberOfPossibleSymbols <= 0) {
            System.out.printf("Error: maximum number of possible symbols in the" +
                    " code is %d (0-9, a-z).", MAX_LENGTH);
            Stop();
        } else if (lengthSecretCode > numberOfPossibleSymbols) {
            System.out.printf("Error: it's not possible to generate a code with" +
                    " a length of %d with %d unique symbols.", lengthSecretCode, numberOfPossibleSymbols);
            Stop();
        }
    }

    private void generateSecretCode() {
        System.out.println("Input the length of the secret code:");
        input = scanner.nextLine();
        if (isCorrectInputNumber(input)) {
            lengthSecretCode = Integer.parseInt(input);
        } else {
            return;
        }
        System.out.println("Input the number of possible symbols in the code");
        input = scanner.nextLine();
        if (isCorrectInputNumber(input)) {
            numberOfPossibleSymbols = Integer.parseInt(input);
        } else {
            return;
        }

        checkCorrectInputBeforeGenerate();
        if (finishGame) {
            return;
        }

        shuffleSymbols();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < lengthSecretCode; i++) {
            stringBuilder.append(chars[i]);
        }
        secretCode = stringBuilder.toString();
        printMessageTheSecretIsPrepared();
        System.out.println("Okay, let's start a game!");
    }

    private void printMessageTheSecretIsPrepared() {
        generateSymbols();
        StringBuilder builder = new StringBuilder();
        builder.append("The secret is prepared: ");
        builder.append("*".repeat(lengthSecretCode));
        builder.append(" ");

        int numbers = 0;
        int letters = 0;
        for (int i = 0; i < numberOfPossibleSymbols; i++) {
            if (Character.isLetter(chars[i])) {
                letters++;
            } else {
                numbers++;
            }
        }
        builder.append("(");
        builder.append(chars[0]);
        if (numbers > 1) {
            builder.append("-");
            builder.append(chars[numbers - 1]);
        }
        if (letters > 0) {
            builder.append(", ");
            builder.append(chars[10]);
            if (letters > 1) {
                builder.append("-");
                builder.append(chars[numbers + letters - 1]);
            }
        }
        builder.append(").");
        System.out.println(builder.toString());
    }

    private void gradeATurn(String input) {
        int cows = 0;
        int bulls = 0;
        for (int j = 0; j < input.length(); j++) {
            if (secretCode.contains(String.valueOf(input.charAt(j)))) {
                if (secretCode.charAt(j) == input.charAt(j)) {
                    bulls++;
                } else {
                    cows++;
                }
            }
        }
        printGrade(bulls, cows);
    }

    private void printGrade(int bulls, int cows) {
        if (cows > 0 & bulls > 0) {
            System.out.printf("Grade: %d %s and %d %s\n",
                    bulls, bulls > 1 ? "bulls" : "bull", cows, cows > 1 ? "cows" : "cow");
        } else if (bulls > 0) {
            System.out.printf("Grade: %d %s\n", bulls, bulls > 1 ? "bulls" : "bull");
        } else if (cows > 0) {
            System.out.printf("Grade: %d %s\n", cows, cows > 1 ? "cows" : "cow");
        } else {
            System.out.println("Grade: None");
        }
    }
}
