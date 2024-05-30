package app.lyricsapp.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Playlist {
    private ArrayList<Song> playlist;
    String playlistFilePath;

    private final String name;

    public Playlist(String name) throws IOException {
        this.name = name;
        playlistFilePath = "src/main/resources/Playlists/" + name + ".json";

        // if file not exist create it
        File playlistFile = new File(playlistFilePath);
        if (!playlistFile.exists()) {
            playlistFile.createNewFile();
            playlist = new ArrayList<>();
            System.out.println("playlist create: " + this.name);
        } else {
            readPlaylist();
        }
    }

    public String getName() {
        return name;
    }

    public void resetPlaylistFile() throws IOException {
        writePlaylist(new ArrayList<>());
    }

    private void writePlaylist(ArrayList<Song> playlist) throws IOException {
        // write playList to playlistFilePath file
        FileWriter playlistFileWriter = new FileWriter(playlistFilePath);

        // if playlist is empty
        if (playlist.size() == 0) {
            playlistFileWriter.write("[]");
            playlistFileWriter.close();
            return;
        }

        // trsform arraylist of songs to json
        StringBuilder content = new StringBuilder();
        for (Song song : playlist) {
            // get id name artist and lyricCheckSum
            content.append(song.toJson()).append(";");
        }
        content = new StringBuilder(content.substring(0, content.length() - 1));
        content = new StringBuilder("[" + content + "]");
        playlistFileWriter.write(content.toString());

        playlistFileWriter.close();
    }

    private void readPlaylist() throws IOException {
        // for each json song in playlistFilePath file

        // read playlistFilePath file
        String playlistFileContent = Files.readString(Path.of(playlistFilePath));
        // if playlistFilePath file is empty
        if (playlistFileContent.equals("[]") || playlistFileContent.equals("")) {
            playlist = new ArrayList<>();
            return;
        }
        // transform json to arraylist of songs
        playlist = new ArrayList<>();
        String[] playlistFileContentArray = playlistFileContent.substring(1, playlistFileContent.length() - 1)
                .split(";");
        // remove {} from each song
        for (String song : playlistFileContentArray) {
            playlist.add(Song.fromJson(song));
        }
    }

    public void addSong(Song music) throws IOException {
        readPlaylist();
        if (playlist.contains(music))
            return;
        playlist.add(music);
        writePlaylist(playlist);
    }

    public void removeSong(Song music) throws IOException {
        readPlaylist();
        // remove if name and artist are the same
        for (Song song : playlist) {
            if (song.getSong().equals(music.getSong()) && song.getArtist().equals(music.getArtist())) {
                playlist.remove(song);
                break;
            }
        }
        writePlaylist(playlist);
    }

    public ArrayList<Song> getPlaylist() throws IOException {
        readPlaylist();
        return playlist;
    }

    public boolean contain(Song music) throws IOException {
        readPlaylist();
        for (Song song : playlist) {
            // if name and artist are the same
            if (song.getSong().equals(music.getSong()) && song.getArtist().equals(music.getArtist())) {
                return true;
            }
        }
        return false;
    }

    public void deletePlaylistFile() {
        File playlistFile = new File(playlistFilePath);
        if (playlistFile.delete()) {
            System.out.println("File deleted successfully");
        } else {
            System.out.println("Failed to delete the file");
        }
    }
}
