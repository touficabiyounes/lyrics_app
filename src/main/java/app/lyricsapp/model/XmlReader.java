package app.lyricsapp.model;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class XmlReader {

    public static ArrayList<?> readXml(StringBuilder content) {
        ArrayList<?> out = new ArrayList<>();
        // remove new lines and tabs between xml tags
        content = new StringBuilder(content.toString().replaceAll("[^>](?![^<]*>|[^<>]*</)", ""));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        {
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(content.toString())));

                NodeList songResults = doc.getElementsByTagName("SearchLyricResult");
                NodeList lyricResult = doc.getElementsByTagName("GetLyricResult");
                if (songResults.getLength() > 0) {
                    out = interpretSongResults(songResults);
                } else if (lyricResult.getLength() > 0) { // get lyrics
                    out = interpretLyricResults(lyricResult);
                }

            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    public static HashMap<String, String> readXmlLang(String content) {
        HashMap<String, String> data = new HashMap<>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            File fXmlFile = new File(content);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList lang = doc.getElementsByTagName("lang");
            for (int temp = 0; temp < lang.getLength(); temp++) {
                Node nNode = lang.item(temp);
                NodeList childs = nNode.getChildNodes();
                for (int j = 0; j < childs.getLength(); j++) {
                    Node n = childs.item(j);
                    if (n.getNodeType() == Node.ELEMENT_NODE) {
                        if (childs.item(j) != null) {
                            data.put(childs.item(j).getNodeName(), childs.item(j).getTextContent());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public static ArrayList<Song> interpretSongResults(NodeList result) {
        ArrayList<Song> songs = new ArrayList<>();
        for (int i = 0; i < result.getLength(); i++) { // for each song
            Node node = result.item(i);
            NodeList childs = node.getChildNodes();

            HashMap<String, String> data = new HashMap<>();
            for (int j = 0; j < childs.getLength(); j++) {
                Node n = childs.item(j);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    if (childs.item(j) != null) {
                        data.put(childs.item(j).getNodeName(), childs.item(j).getTextContent());
                    }
                }
            }
            if (data.get("LyricId") != null) { // check if song has lyrics
                Song song = new Song(data.get("TrackId"), data.get("LyricChecksum"), data.get("LyricId"),
                        data.get("SongUrl"), data.get("ArtistUrl"), data.get("Artist"), data.get("Song"),
                        data.get("SongRank"), data.get("Lyric"), data.get("LyricCovertArtUrl"));
                songs.add(song);
            }
        }
        return songs;
    }

    public static ArrayList<Song> interpretLyricResults(NodeList result) {
        ArrayList<Song> songs = new ArrayList<>();
        for (int i = 0; i < result.getLength(); i++) { // for each song
            Node node = result.item(i);
            NodeList childs = node.getChildNodes();

            HashMap<String, String> data = new HashMap<>();
            for (int j = 0; j < childs.getLength(); j++) {
                Node n = childs.item(j);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    if (childs.item(j) != null) {
                        data.put(childs.item(j).getNodeName(), childs.item(j).getTextContent());
                    }
                }
            }
            if (data.get("LyricId") != null) { // check if song has lyrics
                Song song = new Song(data.get("TrackId"), data.get("LyricChecksum"), data.get("LyricId"),
                        data.get("SongUrl"), data.get("ArtistUrl"), data.get("Artist"), data.get("Song"),
                        data.get("SongRank"), data.get("Lyric"), data.get("LyricCovertArtUrl"));
                songs.add(song);
            }
        }
        return songs;
    }

}
