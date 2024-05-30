package app.lyricsapp.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlaylistsManager {
    private static ArrayList<Playlist> playlists;

    public PlaylistsManager(boolean showFav) throws IOException {
        playlists = new ArrayList<>();
        if (showFav)
            playlists.add(new Favorites());

        loadPlaylists();
    }

    public static void loadPlaylists() throws IOException {
        // for each file in folder playlist init new playslist with file name
        File[] files = new File("src/main/resources/Playlists").listFiles();
        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (fileName.endsWith(".json") && !fileName.equals("Favorites.json")) {
                    String playlistName = fileName.substring(0, fileName.length() - 5);
                    Playlist playlist = new Playlist(playlistName);
                    playlists.add(playlist);
                }
            }
        }
    }

    public static void addPlaylist(String name) throws IOException {
        playlists.add(new Playlist(name));
    }

    public static void removePlaylist(String name) throws IOException {
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(name)) {
                playlists.remove(playlist);
                playlist.resetPlaylistFile();
                break;
            }
        }
    }

    public static ArrayList<Playlist> getPlaylists() {
        return playlists;

    }

    public static Playlist getPlaylist(String name) throws IOException {
        if(name != null){
            if (name.equals("Favorites"))
            return new Favorites();
            for (Playlist playlist : playlists) {
                if (playlist.getName().equals(name)) {
                    return playlist;
                }
            }
        }
        return null;
    }
}
