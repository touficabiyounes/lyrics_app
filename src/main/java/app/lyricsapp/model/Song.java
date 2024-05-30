package app.lyricsapp.model;

import java.util.Objects;

public class Song {
    private final String trackId;
    private final String lyricChecksum;
    private final String lyricId;
    private final String songUrl;
    private final String artistUrl;
    private final String artist;
    private final String song;
    private final String songRank;
    public String lyrics;

    public String lyricCovertArtUrl;
    private String coverArtDefaultUrl;

    public Song(String trackId, String lyricChecksum, String lyricId, String songUrl, String artistUrl, String artist,
            String song, String songRank, String lyrics, String lyricCovertArtUrl) {
        this.trackId = trackId;
        this.lyricChecksum = lyricChecksum;
        this.lyricId = lyricId;
        this.songUrl = songUrl;
        this.artistUrl = artistUrl;
        this.artist = artist;
        this.song = song;
        this.songRank = songRank;
        this.lyrics = lyrics;
        this.lyricCovertArtUrl = lyricCovertArtUrl;
        this.coverArtDefaultUrl = "https://discussions.apple.com/content/attachment/592590040";
    }

    public Song(String id, String name, String artist, String lyricCheckSum) {
        this.trackId = "";
        this.lyricChecksum = lyricCheckSum;
        this.lyricId = id;
        this.songUrl = "";
        this.artistUrl = "";
        this.artist = artist;
        this.song = name;
        this.songRank = "";
        this.lyrics = "";
        this.lyricCovertArtUrl = "";
    }

    public String getTrackId() {
        return trackId;
    }

    public String getLyricChecksum() {
        return lyricChecksum;
    }

    public String getLyricId() {
        return lyricId;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public String getArtistUrl() {
        return artistUrl;
    }

    public String getArtist() {
        return artist;
    }

    public String getSong() {
        return song;
    }

    public String getSongRank() {
        return songRank;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String getLyricCovertArtUrl() {
        return lyricCovertArtUrl;
    }

    public String toJson() {
        return "{" +
                "\"lyricChecksum\":\"" + lyricChecksum + '\"' +
                ", \"lyricId\":\"" + lyricId + '\"' +
                ", \"artist\":\"" + artist + '\"' +
                ", \"song\":\"" + song + '\"' +
                ", \"songRank\":\"" + songRank + '\"' +
                '}';
    }

    public static Song fromJson(String json) {
        json = json.replace("{", "");
        json = json.replace("}", "");
        json = json.replace("\"", "");
        String[] parts = json.split(",");
        String lyricChecksum = parts[0].split(":")[1];
        String lyricId = parts[1].split(":")[1];
        String artist = parts[2].split(":")[1];
        String song = parts[3].split(":")[1];
        return new Song("", lyricChecksum, lyricId, "", "", artist, song, "", "", "");
    }

    @Override
    public String toString() {
        return "Artist: " + artist + ", Title: " + song;
    }

    // method equal
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Song.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Song other = (Song) obj;

        if (!Objects.equals(this.lyricChecksum, other.lyricChecksum)) {
            return false;
        }
        if (!Objects.equals(this.lyricId, other.lyricId)) {
            return false;
        }
        if (!Objects.equals(this.artist, other.artist)) {
            return false;
        }
        return Objects.equals(this.song, other.song);
    }

    public String getCoverArtUrl() {
        if (lyricCovertArtUrl != null && !lyricCovertArtUrl.isEmpty()) {
            return lyricCovertArtUrl;
        } else if (lyricId != null && !lyricId.isEmpty() && lyricChecksum != null && !lyricChecksum.isEmpty()) {
            String URL = Search.getCoverArtUrl(this.lyricId, this.lyricChecksum);
            if (URL != null && !URL.isEmpty()) {
                return URL;
            } else {
                return coverArtDefaultUrl;
            }
        } else {
            return coverArtDefaultUrl;
        }
    }

}
