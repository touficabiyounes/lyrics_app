package app.lyricsapp.model;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class SongCover {

    public static void coverImage(String LyricCovertArtUrl) {
        Image image = null;
        try {
            URL url = new URL(LyricCovertArtUrl);
            image = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame();
        frame.setSize(300, 300);
        JLabel label = new JLabel(new ImageIcon(image));
        frame.add(label);
        frame.setVisible(true);
    }

    // public static void main(String[] args) {
    // SongCover.coverImage("http://ec1.images-amazon.com/images/P/B000CNET66.02.MZZZZZZZ.jpg");
    // }
}
