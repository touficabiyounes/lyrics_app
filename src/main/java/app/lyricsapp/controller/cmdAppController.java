package app.lyricsapp.controller;

import app.lyricsapp.model.Favorites;
import app.lyricsapp.model.Search;
import app.lyricsapp.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class cmdAppController {
    public static Scanner scanner = new Scanner(System.in);

    public void launch() {
        mainMenu();
    }

    public void mainMenu() {
        ArrayList<HashMap<String, String>> mainMenuItems = new ArrayList<>();
        HashMap<String, String> searchByTitle = new HashMap<>();
        searchByTitle.put("title", "Search by title & artist");
        searchByTitle.put("search", "title/artist");
        searchByTitle.put("class", "app.lyricsapp.model.Search");
        searchByTitle.put("method", "searchByTitleArtist");
        searchByTitle.put("result", "ArrayList<Song>");
        mainMenuItems.add(searchByTitle);

        HashMap<String, String> searchByLyrics = new HashMap<>();
        searchByLyrics.put("title", "Search by lyrics");
        searchByLyrics.put("search", "lyrics");
        searchByLyrics.put("class", "app.lyricsapp.model.Search");
        searchByLyrics.put("method", "searchByLyrics");
        searchByLyrics.put("result", "ArrayList<Song>");
        mainMenuItems.add(searchByLyrics);

        HashMap<String, String> accessFavorites = new HashMap<>();
        accessFavorites.put("title", "Access favorites");
        accessFavorites.put("search", "false");
        accessFavorites.put("class", "app.lyricsapp.model.Favorites");
        accessFavorites.put("method", "getFavList");
        accessFavorites.put("result", "ArrayList<Song>");
        mainMenuItems.add(accessFavorites);

        System.out.println("--------------------");
        showMenu(mainMenuItems, "Main menu");
        getChoice(mainMenuItems);
    }

    public void songsMenu(ArrayList<Song> songs) throws IOException {
        if (songs.size() == 0) {
            System.out.println("No songs found");
            mainMenu();
        }

        int choice = navigateSongsMenu(songs, 1);
        if (choice > 0 && choice <= songs.size()) {
            Song songChoice = songs.get(choice - 1);
            System.out.println("--------------------");
            System.out.println("You chose " + songChoice.getSong() + " by " + songChoice.getArtist());
            if (Favorites.isFav(songChoice)) {
                System.out.println("1. Remove from favorites");
            } else {
                System.out.println("1. Add to favorites");
            }
            System.out.println("2. Show lyrics");
            System.out.println("3. Back to main menu");
            System.out.print("Enter your choice: ");
            int choice2 = scanInt(1, 3);
            if (choice2 == 1) {
                if (Favorites.isFav(songChoice)) {
                    Favorites.removeFav(songChoice);
                    System.out.println("Removed from favorites");
                } else {
                    Favorites.addFav(songChoice);
                    System.out.println("Added to favorites");
                }
                mainMenu();
            } else if (choice2 == 2) {
                if (!Objects.equals(songChoice.getLyricId(), "0") && songChoice.getLyricChecksum() != null) {
                    String lyrics = Search.searchLyric(songChoice.getLyricId(), songChoice.getLyricChecksum());
                    for (int i = 1; i < lyrics.split(" ").length; i++) {
                        System.out.print(lyrics.split(" ")[i - 1] + " ");
                        if (i % 10 == 0) {
                            System.out.println();
                        }
                    }
                    System.out.println();
                    System.out.print("Back to main menu...");
                    scanner.nextLine();
                    mainMenu();
                } else {
                    System.out.println("Lyrics not found");
                    mainMenu();
                }
            } else if (choice2 == 3) {
                mainMenu();
            } else {
                System.out.print("Invalid choice");
                songsMenu(songs);
            }
        } else {
            System.out.println("back to main menu");
            songsMenu(songs);
        }
    }

    public void showMenu(ArrayList<HashMap<String, String>> items, String title) {
        System.out.println(title);
        for (int i = 0; i < items.size(); i++) {
            System.out.println(i + 1 + ". " + items.get(i).get("title"));
        }
    }

    public void getChoice(ArrayList<HashMap<String, String>> items) {
        System.out.print("Enter your choice: ");
        int choice = scanInt(1, items.size());
        for (int i = 0; i < items.size(); i++) {
            if (choice == i + 1) {
                String className = items.get(i).get("class");
                String method = items.get(i).get("method");
                String search = items.get(i).get("search");
                String result = items.get(i).get("result");
                ArrayList<String> params;

                if (!search.equals("false")) {
                    int paramsLength = search.split("/").length;
                    params = new ArrayList<>();
                    for (int j = 0; j < paramsLength; j++) {
                        System.out.print("Enter " + items.get(i).get("search").split("/")[j] + ": ");
                        params.add(scanner.nextLine());
                    }
                } else {
                    params = null;
                }

                // call className.method(search)
                try {
                    Class<?> cls = Class.forName(className);
                    Object obj = cls.newInstance();
                    if (params == null) {
                        Object resultObj = cls.getMethod(method).invoke(obj);
                        if (Objects.equals(result, "ArrayList<Song>")) {
                            songsMenu((ArrayList<Song>) resultObj);
                        }
                    } else if (params.size() == 2) {
                        Object resultObj = cls.getMethod(method, ArrayList.class).invoke(obj, params);
                        if (Objects.equals(result, "ArrayList<Song>")) {
                            songsMenu((ArrayList<Song>) resultObj);
                        }
                    } else {
                        Object resultObj = cls.getMethod(method, String.class).invoke(obj, params.get(0));
                        if (Objects.equals(result, "ArrayList<Song>")) {
                            songsMenu((ArrayList<Song>) resultObj);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int scanInt(int min, int max) {
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.print("Invalid input, try again: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input, try again: ");
            }
        }
    }

    public int navigateSongsMenu(ArrayList<Song> songs, int page) {
        System.out.println("--- Songs ---");
        // 10 songs per page
        int maxPage = (int) Math.ceil((double) songs.size() / 10);
        if (page > maxPage) {
            page = maxPage;
        }
        if (page < 1) {
            page = 1;
        }
        int start = (page - 1) * 10;
        int end = start + 10;
        if (end > songs.size()) {
            end = songs.size();
        }
        for (int i = start; i < end; i++) {
            System.out.println(i + 1 + ". " + songs.get(i).getSong() + " by " + songs.get(i).getArtist());
        }

        // if < 10 songs
        if (songs.size() < 10) {
            System.out.print("Enter your choice: ");
            return scanInt(1, songs.size());
        }
        System.out.println("page " + page + " of " + maxPage + " pages" + " (" + songs.size() + " songs)");

        // get choice for navigate to previous or next page or select song
        System.out.println("--------------------");
        System.out.println("1. Previous page");
        System.out.println("2. Next page");
        System.out.println("3. Select song");
        System.out.println("4. Back to main menu");
        System.out.print("Enter your choice: ");

        int choice = scanInt(1, 4);
        if (choice == 1) {
            if (page > 1) {
                page--;
                navigateSongsMenu(songs, page);
            } else {
                System.out.println("Already at first page");
                navigateSongsMenu(songs, page);
            }
        } else if (choice == 2) {
            if (page < maxPage) {
                page++;
                navigateSongsMenu(songs, page);
            } else {
                System.out.println("Already at last page");
                navigateSongsMenu(songs, page);
            }
        } else if (choice == 3) {
            System.out.print("Select a song : ");
            return scanInt(1, songs.size());
        } else if (choice == 4) {
            mainMenu();
        }
        return 0;
    }

    public static void main(String[] args) {
        cmdAppController controller = new cmdAppController();
        controller.launch();
    }
}
