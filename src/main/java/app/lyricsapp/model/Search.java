package app.lyricsapp.model;

import java.util.ArrayList;

public class Search {
    public static ArrayList<Song> searchByTitleArtist(ArrayList<String> parameters) {
        String title = parameters.get(0);
        String artist = parameters.get(1);
        title = title.replace(" ", "%20");
        artist = artist.replace(" ", "%20");

        StringBuilder content = ApiRequest
                .request("http://api.chartlyrics.com/apiv1.asmx/SearchLyric?artist=" + artist + "&song=" + title);
                ArrayList<?> rawList = XmlReader.readXml(content);
                ArrayList<Song> results = new ArrayList<Song>();
                for (Object o : rawList) {
                    if (o instanceof Song) {
                        results.add((Song) o);
                    }
                }
        return results;
    }

    public static ArrayList<Song> searchByLyrics(String lyrics) {
        lyrics = lyrics.replace(" ", "%20");
        StringBuilder content = ApiRequest
                .request("http://api.chartlyrics.com/apiv1.asmx/SearchLyricText?lyricText=" + lyrics);

                ArrayList<?> rawList = XmlReader.readXml(content);
                ArrayList<Song> results = new ArrayList<Song>();
                for (Object o : rawList) {
                    if (o instanceof Song) {
                        results.add((Song) o);
                    }
                }
        return results;
    }

    public static String unEscapeString(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++)
            switch (s.charAt(i)) {
                case '\n':
                    sb.append("\\n");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                // ... rest of escape characters
                default:
                    sb.append(s.charAt(i));
            }
        return sb.toString();
    }

    public static String searchLyric(String lyricId, String lyricCheckSum) {
        StringBuilder content = ApiRequest.request("http://api.chartlyrics.com/apiv1.asmx/GetLyric?lyricId=" + lyricId
                + "&lyricCheckSum=" + lyricCheckSum + "");
                ArrayList<?> rawList = XmlReader.readXml(content);
                ArrayList<Song> results = new ArrayList<Song>();
                for (Object o : rawList) {
                    if (o instanceof Song) {
                        results.add((Song) o);
                    }
                }
        if (results.size() == 0) {
            return "No lyrics found";
        }

        return results.get(0).getLyrics();
    }

    public static String getCoverArtUrl(String lyricId, String lyricCheckSum) {
        if (lyricId.equals("") || lyricCheckSum.equals("") || lyricId.equals("0") || lyricCheckSum.equals("0")
                || lyricId.equals("null") || lyricCheckSum.equals("null")) {
            return null;
        }
        StringBuilder content = ApiRequest.request("http://api.chartlyrics.com/apiv1.asmx/GetLyric?lyricId=" + lyricId
                + "&lyricCheckSum=" + lyricCheckSum + "");
        ArrayList<?> rawList = XmlReader.readXml(content);
        ArrayList<Song> results = new ArrayList<Song>();
        for (Object o : rawList) {
            if (o instanceof Song) {
                results.add((Song) o);
            }
        }
        if (results.size() == 0) {
            return null;
        }
        return results.get(0).getCoverArtUrl();
    }

}
