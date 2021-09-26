package org.hyperskill.app;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.Start();
        while (!game.isFinishGame()) {
            game.nextTurn();
        }
    }
}