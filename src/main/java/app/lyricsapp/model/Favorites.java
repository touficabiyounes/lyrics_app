package app.lyricsapp.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

import static java.util.Collections.reverseOrder;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

public class Favorites extends Playlist {
    private static ArrayList<Song> favList;
    private static final String favFilePath = "src/main/resources/playlists/Favorites.json";

    public Favorites() throws IOException {
        super("Favorites");

        if (!Files.exists(Path.of(favFilePath))) {
            createFavorites();
        }

    }

    public static void createFavorites() throws IOException {
        // create favFilePath file
        FileWriter favFileWriter = new FileWriter(favFilePath);
        favFileWriter.write("[]");
        favFileWriter.close();
    }

    @Override
    public String getName() {
        return "Favorites";
    }

    private static void writeFav(ArrayList<Song> favList) throws IOException {
        // write favList to favFilePath file
        FileWriter favFileWriter = new FileWriter(favFilePath);

        // if favlist is empty
        if (favList.size() == 0) {
            favFileWriter.write("[]");
            favFileWriter.close();
            return;
        }

        // trsform arraylist of songs to json
        String content = "";
        for (Song song : favList) {
            // get id name artist and lyricCheckSum
            content += song.toJson() + ";";
        }
        content = content.substring(0, content.length() - 1);
        content = "[" + content + "]";
        favFileWriter.write(content);

        favFileWriter.close();
    }

    private static void readFav() throws IOException {
        // for each json song in favFilePath file
        if (!Files.exists(Path.of(favFilePath))) {
            createFavorites();
        }
        // read favFilePath file
        String favFileContent = Files.readString(Path.of(favFilePath));
        // if favFilePath file is empty
        if (favFileContent.equals("[]") || favFileContent.equals("")) {
            favList = new ArrayList<>();
            return;
        }
        // transform json to arraylist of songs
        favList = new ArrayList<>();
        String[] favFileContentArray = favFileContent.substring(1, favFileContent.length() - 1).split(";");
        // remove {} from each song
        for (String song : favFileContentArray) {
            favList.add(Song.fromJson(song));
        }
    }

    public static void addFav(Song fav) throws IOException {
        readFav();
        if (favList.contains(fav))
            return;
        favList.add(fav);
        writeFav(favList);
    }

    public static void removeFav(Song fav) throws IOException {
        readFav();
        // remove if name and artist are the same
        for (Song song : favList) {
            if (song.getSong().equals(fav.getSong()) && song.getArtist().equals(fav.getArtist())) {
                favList.remove(song);
                break;
            }
        }
        writeFav(favList);
    }

    public static List<Song> getFavList() throws IOException {
        readFav();
        return favList;
    }

    public static boolean isFav(Song fav) throws IOException {
        readFav();
        for (Song song : favList) {
            // if name and artist are the same
            if (song.getSong().equals(fav.getSong()) && song.getArtist().equals(fav.getArtist())) {
                return true;
            }
        }
        return false;
    }

    // return top a rank of mosts present artists
    public static List<List<String>> getTopArtists() throws IOException {
        List<String> artists = new ArrayList<>();

        for (Song song : getFavList()) {
            artists.add(song.getArtist());
        }

        // get top artists sorted
        List<String> result = artists.stream()
                .collect(groupingBy(identity(), counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(7)
                .map(Map.Entry::getKey)
                .collect(toList());

        // get number of songs for each artist
        HashMap<String, Integer> map = new HashMap<>();
        for (String artist : result) {
            for (Song song : getFavList()) {
                if (song.getArtist().equals(artist)) {
                    if (map.containsKey(artist)) {
                        map.put(artist, map.get(artist) + 1);
                    } else {
                        map.put(artist, 1);
                    }
                }
            }
        }

        // get tupple artist+number of songs
        List<List<String>> list = new ArrayList<>();
        for (String artist : result) {
            List<String> temp = new ArrayList<>();
            temp.add(artist);
            temp.add(map.get(artist).toString());
            list.add(temp);
        }

        return list;
    }
}
