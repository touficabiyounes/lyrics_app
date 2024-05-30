package app.lyricsapp;

import app.lyricsapp.controller.cmdAppController;


public class LyricsAppCLI {
    public static void main(String[] args) {
        System.out.println("Welcome to the lyrics app");

        cmdAppController controller = new cmdAppController();
        controller.launch();

    }
}
