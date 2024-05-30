package app.lyricsapp.controller;

import app.lyricsapp.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;
import java.util.List;

import static java.lang.Integer.parseInt;

public class LyricsAppController extends Application {

    private boolean showCover = false;
    ComboBox comboxTop;
    private static String languageChoice = "English";
    private static List<Object> translateListRoot;
    private static List<Object> translateListDetails;

    private static Song songSelected;

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader
                .load(Objects.requireNonNull(getClass().getResource("/app/lyricsapp/view/lyricsapp.fxml")));
        AnchorPane root2 = FXMLLoader
                .load(Objects.requireNonNull(getClass().getResource("/app/lyricsapp/view/songDetails.fxml")));
        primaryStage.setTitle("LyricsFuze");
        // set app icon
        Image icon = new Image("icon.png");
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
        // get elements
        Button search = (Button) root.lookup("#search");
        Button searchLyrics = (Button) root.lookup("#searchLyrics");
        TextField title = (TextField) root.lookup("#title");
        TextField artist = (TextField) root.lookup("#artist");
        TextField lyrics = (TextField) root.lookup("#lyrics");
        comboxTop = (ComboBox) root.lookup("#playlists");
        Label titleText = (Label) root.lookup("#titleText");
        Label artistText = (Label) root.lookup("#artistText");
        Label playlistText = (Label) root.lookup("#playlistText");
        Label lyricsText = (Label) root.lookup("#lyricsText");
        Label rankMin = (Label) root.lookup("#rankMin");
        TextField minRankInput = (TextField) root.lookup("#minRankInput");
        Button removePlaylist = (Button) root.lookup("#removePlaylist");
        Button addPlaylist = (Button) root.lookup("#addPlaylist");
        TextField newPlaylistName = (TextField) root.lookup("#newPlaylistName");
        Label fav = (Label) root2.lookup("#favLabel");

        MenuBar menuBar = (MenuBar) root.lookup("#menuBar");

        Menu prefMenu = menuBar.getMenus().get(0);

        // set min rank option
        CheckMenuItem minRankOption = (CheckMenuItem) prefMenu.getItems().get(0);
        minRankOption.setOnAction(event -> {
            if (minRankOption.isSelected()) {
                rankMin.setVisible(true);
                minRankInput.setVisible(true);
            } else {
                rankMin.setVisible(false);
                minRankInput.setVisible(false);
            }
        });

        // set show cover option
        CheckMenuItem showCoverOption = (CheckMenuItem) prefMenu.getItems().get(1);
        showCoverOption.setOnAction(event -> {
            showCover = !showCover;
        });

        // set language option
        Menu languageMenu = menuBar.getMenus().get(2);
        MenuItem english = languageMenu.getItems().get(0);
        MenuItem french = languageMenu.getItems().get(1);
        MenuItem spanish = languageMenu.getItems().get(2);
        Menu artistMenu = menuBar.getMenus().get(1);
        translateListRoot = Arrays.asList(titleText, artistText, playlistText, lyricsText, search, searchLyrics,
                rankMin, minRankOption, showCoverOption, artistMenu, comboxTop, prefMenu, removePlaylist, addPlaylist,
                newPlaylistName, languageMenu);
        english.setOnAction(event -> {
            System.out.println("Set English");
            languageChoice = "English";
            translate();
        });
        french.setOnAction(event -> {
            System.out.println("Set French");
            languageChoice = "Français";
            translate();
        });
        spanish.setOnAction(event -> {
            System.out.println("Set Spanish");
            languageChoice = "Español";
            translate();
        });

        // add top artists to menu
        List<List<String>> topArtists = Favorites.getTopArtists();
        Integer rank = 1;
        for (List artistName : topArtists) {
            MenuItem artistItem = new MenuItem(rank + " - " + artistName.get(0) + " (" + artistName.get(1) + ")");
            artistMenu.getItems().add(artistItem);
            rank++;
        }

        // refresh top artists
        artistMenu.setOnShowing(event -> {
            artistMenu.getItems().clear();
            List<List<String>> topArtistsTemp;
            try {
                topArtistsTemp = Favorites.getTopArtists();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Integer rankTemp = 1;
            for (List artistName : topArtistsTemp) {
                MenuItem artistItem = new MenuItem(
                        rankTemp + " - " + artistName.get(0) + " (" + artistName.get(1) + ")");
                artistMenu.getItems().add(artistItem);
                rankTemp++;
            }
        });

        // add event listeners for on enter key press
        title.setOnAction(event -> artist.requestFocus());
        artist.setOnAction(event -> search.fire());

        lyrics.setOnAction(event -> searchLyrics.fire());
        // set on action
        search.setOnAction(event -> {
            String titleSearch = title.getText();
            String artistSearch = artist.getText();
            if (titleSearch.isEmpty() || artistSearch.isEmpty()) {
                System.out.println("Please enter a title and artist");
                return;
            }
            if (minRankOption.isSelected()) {
                // if not a number
                if (!minRankInput.getText().matches("\\d+")) {
                    System.out.println("Please enter a number");
                    minRankInput.setText("");
                    minRankInput.requestFocus();
                    return;
                }
                searchByTitleArtist(root, titleSearch, artistSearch, parseInt(minRankInput.getText()));
            } else {
                searchByTitleArtist(root, titleSearch, artistSearch, 0);
            }

        });

        searchLyrics.setOnAction(event -> {
            String lyricsSearch = lyrics.getText();
            if (lyricsSearch.isEmpty()) {
                System.out.println("Please enter lyrics");
                return;
            }
            searchByLyrics(root, lyricsSearch);
        });

        // add playlists to combobox
        PlaylistsManager playlistsManager = new PlaylistsManager(true);
        ArrayList<Playlist> playlists = PlaylistsManager.getPlaylists();
        // add in combo box all playlists name
        for (Playlist playlist1 : playlists) {
            comboxTop.getItems().add(playlist1.getName());
        }

        // add event listener for playlist combobox
        comboxTop.setOnAction(event -> {
            try {
                String playlistName = (String) comboxTop.getValue();
                comboxTop.setValue(null);
                Playlist selectedPlaylist;

                selectedPlaylist = PlaylistsManager.getPlaylist(playlistName);

                if (selectedPlaylist == null) {
                    System.out.println("Playlist not found");
                    return;
                }
                ArrayList<Song> songs;

                songs = selectedPlaylist.getPlaylist();
                renderResults(root, songs, selectedPlaylist);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

    }

    public static void main(String[] args) {
        launch(args);
    }

    public void getCoverArt(ImageView imageView, Song song) {
        imageView.setImage(new Image(song.getCoverArtUrl()));
    }

    public void searchByTitleArtist(Parent root, String title, String artist, int minRank) {
        System.out.println("Searching for " + title + " by " + artist);
        ArrayList<Song> results = Search.searchByTitleArtist(new ArrayList<>(List.of(title, artist)));
        if (minRank != 0) {
            ArrayList<Song> resultsWithMinRank = new ArrayList<>();
            for (Song song : results) {
                if (parseInt(song.getSongRank()) >= minRank) {
                    resultsWithMinRank.add(song);
                }
            }
            results = resultsWithMinRank;
        }
        System.out.println("Found " + results.size() + " results");
        renderResults(root, results, null);
    }

    public void searchByLyrics(Parent root, String lyrics) {
        System.out.println("Searching for " + lyrics);
        ArrayList<Song> results = Search.searchByLyrics(lyrics);
        System.out.println("Found " + results.size() + " results");
        renderResults(root, results, null);
    }

    public void renderResults(Parent root, ArrayList<Song> results, Playlist playlistSelected) {
        ScrollPane scrollPane = (ScrollPane) root.lookup("#scrollPane");
        GridPane resultsPane = (GridPane) root.lookup("#resultsPane");
        resultsPane.getChildren().clear();
        resultsPane.setHgap(10);
        resultsPane.setVgap(10);
        scrollPane.setOpaqueInsets(new javafx.geometry.Insets(0, 0, 0, 0));
        scrollPane.setFitToWidth(true);
        Integer columns = 5;
        Integer i = 0;

        AnchorPane playlistMenu = (AnchorPane) root.lookup("#playlistMenu");
        if (playlistSelected != null) {
            System.out.println("Playlist selected");
            Label playlistName = (Label) playlistMenu.lookup("#playlistName");
            Button removePlaylist = (Button) playlistMenu.lookup("#removePlaylist");
            removePlaylist.setOnAction(event -> {

                System.out.println("Removing playlist " + playlistSelected.getName());
                if (playlistSelected.getName().equals("Favorites")) {
                    try {
                        playlistSelected.resetPlaylistFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    playlistSelected.deletePlaylistFile();
                    comboxTop.getItems().remove(playlistSelected.getName());
                }

                playlistMenu.setVisible(false);
                resultsPane.getChildren().clear();
            });
            TextField playlistNameField = (TextField) playlistMenu.lookup("#newPlaylistName");
            Button addPlaylist = (Button) playlistMenu.lookup("#addPlaylist");
            addPlaylist.setOnAction(event -> {
                String newPlaylistName = playlistNameField.getText();
                if (newPlaylistName.isEmpty()) {
                    System.out.println("Please enter a name");
                    return;
                }
                try {
                    PlaylistsManager.addPlaylist(newPlaylistName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                comboxTop.getItems().add(newPlaylistName);
                System.out.println("Added playlist " + newPlaylistName);
                playlistNameField.setText("");
                playlistName.setText(newPlaylistName);
                try {
                    Playlist selectedPlaylist = PlaylistsManager.getPlaylist(newPlaylistName);
                    ArrayList<Song> songs = selectedPlaylist.getPlaylist();
                    renderResults(root, songs, selectedPlaylist);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            playlistName.setText(playlistSelected.getName());
            playlistMenu.setVisible(true);
        } else {
            playlistMenu.setVisible(false);
        }

        for (Song song : results) {
            AnchorPane resultTemplate;
            try {
                resultTemplate = FXMLLoader
                        .load(Objects.requireNonNull(getClass().getResource("/app/lyricsapp/view/songResult.fxml")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // modify template
            ((Label) resultTemplate.lookup("#songTitle")).setText(song.getSong());
            ((Label) resultTemplate.lookup("#songArtist")).setText(song.getArtist());

            if (showCover) {
                resultTemplate.lookup("#loadCover").setVisible(false);
                getCoverArt(((ImageView) resultTemplate.lookup("#coverArt")), song);
            } else {
                ((Button) resultTemplate.lookup("#loadCover")).setOnAction(event -> {
                    resultTemplate.lookup("#loadCover").setVisible(false);
                    getCoverArt(((ImageView) resultTemplate.lookup("#coverArt")), song);
                    borderRadiusImage((ImageView) resultTemplate.lookup("#coverArt"), 20);
                });
            }
            borderRadiusImage((ImageView) resultTemplate.lookup("#coverArt"), 20);

            resultsPane.add(resultTemplate, i % columns, i / columns);
            i++;

            // on click render songDetails
            resultTemplate.setOnMouseClicked(event -> {
                try {
                    renderDetails(root, song);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void renderDetails(Parent root, Song song) throws IOException {
        ScrollPane scrollPane = (ScrollPane) root.lookup("#scrollPane");
        GridPane resultsPane = (GridPane) root.lookup("#resultsPane");
        resultsPane.getChildren().clear();
        resultsPane.setHgap(10);
        resultsPane.setVgap(10);
        scrollPane.setOpaqueInsets(new javafx.geometry.Insets(0, 0, 0, 0));
        scrollPane.setFitToWidth(true);

        AnchorPane resultTemplate;
        try {
            resultTemplate = FXMLLoader
                    .load(Objects.requireNonNull(getClass().getResource("/app/lyricsapp/view/songDetails.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        songSelected = song;
        ((Label) resultTemplate.lookup("#songTitle")).setText(song.getSong());
        ((Label) resultTemplate.lookup("#songArtist")).setText(song.getArtist());
        ((Label) resultTemplate.lookup("#songRank")).setText("Rank : " + song.getSongRank());

        ((Button) resultTemplate.lookup("#favButton")).setText(Favorites.isFav(song) ? "Unfav" : "Fav");
        AnchorPane finalResultTemplate = resultTemplate;
        ((Button) resultTemplate.lookup("#favButton")).setOnAction(event -> {
            try {
                if (Favorites.isFav(song)) {
                    Favorites.removeFav(song);
                    ((Button) finalResultTemplate.lookup("#favButton")).setText("Fav");
                } else {
                    Favorites.addFav(song);
                    ((Button) finalResultTemplate.lookup("#favButton")).setText("unFav");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ImageView imageView = ((ImageView) finalResultTemplate.lookup("#coverArt"));

        getCoverArt(imageView, song);

        ((TextArea) finalResultTemplate.lookup("#songLyrics"))
                .setText(Search.searchLyric(song.getLyricId(), song.getLyricChecksum()));

        // menu add/remove from playlist
        ComboBox playlist = (ComboBox) finalResultTemplate.lookup("#playlistBox");
        Label playlistLabelRem = (Label) finalResultTemplate.lookup("#playlistLabelRem");
        Label playlistLabelAdd = (Label) finalResultTemplate.lookup("#playlistLabelAdd");
        Label playlistLabelName = (Label) finalResultTemplate.lookup("#playlistLabelName");
        playlistLabelRem.setVisible(false);
        playlistLabelAdd.setVisible(false);

        PlaylistsManager playlistsManager = new PlaylistsManager(false);
        ArrayList<Playlist> playlists = PlaylistsManager.getPlaylists();
        for (Playlist playlist1 : playlists) {
            playlist.getItems().add(playlist1.getName());
        }
        playlist.setOnAction(event -> {
            // get selected playlist and if song is not in it add it or remove it
            String playlistName = (String) playlist.getValue();
            playlist.setValue(null);
            Playlist selectedPlaylist = null;
            for (Playlist playlist1 : playlists) {
                if (playlist1.getName().equals(playlistName)) {
                    selectedPlaylist = playlist1;
                    break;
                }
            }
            if (selectedPlaylist == null) {
                return;
            }
            try {
                if (selectedPlaylist.contain(song)) {
                    selectedPlaylist.removeSong(song);
                    playlistLabelRem.setVisible(true);
                    playlistLabelName.setText(playlistName);
                    playlistLabelName.setVisible(true);
                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), evt -> {
                        playlistLabelRem.setVisible(false);
                        playlistLabelName.setVisible(false);
                    }));
                    timeline.play();

                } else {
                    selectedPlaylist.addSong(song);
                    playlistLabelAdd.setVisible(true);
                    playlistLabelName.setText(playlistName);
                    playlistLabelName.setVisible(true);
                    // hide label after 2 seconds
                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), evt -> {
                        playlistLabelAdd.setVisible(false);
                        playlistLabelName.setVisible(false);
                    }));
                    timeline.play();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        Label favLabel = (Label) resultTemplate.lookup("#favLabel");
        Label songRank = (Label) resultTemplate.lookup("#songRank");

        // set language option
        translateListDetails = Arrays.asList(favLabel, playlist, songRank, playlistLabelRem, playlistLabelAdd);

        translate();
        resultsPane.add(finalResultTemplate, 0, 0);
    }

    public static void borderRadiusImage(ImageView imageView, int borderRadius) {
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(borderRadius);
        clip.setArcHeight(borderRadius);
        imageView.setClip(clip);
        // snapshot the rounded image.
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = imageView.snapshot(parameters, null);
        // set the modified image
        imageView.setClip(null);
        imageView.setImage(image);
    }

    public static void translateRoot(HashMap<String, String> data) {
        Label titleText = (Label) translateListRoot.get(0);
        Label artistText = (Label) translateListRoot.get(1);
        Label playlistText = (Label) translateListRoot.get(2);
        Label lyricsText = (Label) translateListRoot.get(3);
        Button search = (Button) translateListRoot.get(4);
        Button searchLyrics = (Button) translateListRoot.get(5);
        Label rankMin = (Label) translateListRoot.get(6);
        CheckMenuItem minRankOption = (CheckMenuItem) translateListRoot.get(7);
        MenuItem showCoverOption = (MenuItem) translateListRoot.get(8);
        MenuItem artistMenu = (MenuItem) translateListRoot.get(9);
        ComboBox<String> comboxTop = (ComboBox<String>) translateListRoot.get(10);
        Menu prefMenu = (Menu) translateListRoot.get(11);
        Button removePlaylist = (Button) translateListRoot.get(12);
        Button addPlaylist = (Button) translateListRoot.get(13);
        TextField newPlaylistName = (TextField) translateListRoot.get(14);
        Menu languageMenu = (Menu) translateListRoot.get(15);

        titleText.setText(data.get("title"));
        lyricsText.setText(data.get("lyrics"));
        artistText.setText(data.get("artist"));
        playlistText.setText(data.get("playlists"));
        search.setText(data.get("search"));
        searchLyrics.setText(data.get("search"));
        rankMin.setText(data.get("rankMin"));
        minRankOption.setText(data.get("rankMin"));
        showCoverOption.setText(data.get("showCover"));
        artistMenu.setText(data.get("artistFav"));
        comboxTop.setPromptText(data.get("playlistSelect"));
        prefMenu.setText(data.get("preferences"));
        removePlaylist.setText(data.get("removePlaylist"));
        addPlaylist.setText(data.get("addPlaylist"));
        newPlaylistName.setPromptText(data.get("newPlaylistname"));
        languageMenu.setText(data.get("language"));
    }

    public static void translateSongDetails(HashMap<String, String> data, Song song) {
        Label favLabel = (Label) translateListDetails.get(0);
        ComboBox<String> playlistBox = (ComboBox<String>) translateListDetails.get(1);
        Label songRank = (Label) translateListDetails.get(2);
        Label playlistLabelRem = (Label) translateListDetails.get(3);
        Label playlistLabelAdd = (Label) translateListDetails.get(4);

        favLabel.setText(data.get("add_rem"));
        playlistBox.setPromptText(data.get("playlist"));
        songRank.setText(data.get("songRank") + song.getSongRank());
        playlistLabelRem.setText(data.get("playlistLabelRem"));
        playlistLabelAdd.setText(data.get("playlistLabelAdd"));
    }

    public static void translate() {
        if (languageChoice == "English") {
            translateRoot(XmlReader.readXmlLang("src/main/resources/languages/EnglishLang.xml"));
            if (songSelected != null) {
                translateSongDetails(XmlReader.readXmlLang("src/main/resources/languages/EnglishLang.xml"),
                        songSelected);
            }
        } else if (languageChoice == "Français") {
            translateRoot(XmlReader.readXmlLang("src/main/resources/languages/FrenchLang.xml"));
            if (songSelected != null) {
                translateSongDetails(XmlReader.readXmlLang("src/main/resources/languages/FrenchLang.xml"),
                        songSelected);
            }
        } else if (languageChoice == "Español") {
            translateRoot(XmlReader.readXmlLang("src/main/resources/languages/SpanishLang.xml"));
            if (songSelected != null) {
                translateSongDetails(XmlReader.readXmlLang("src/main/resources/languages/SpanishLang.xml"),
                        songSelected);
            }
        }
    }

}
