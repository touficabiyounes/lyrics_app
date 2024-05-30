package app.lyricsapp.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiRequest {
    public static StringBuilder request(String urlApi) {
        try {
            // GET request to urlApi
            URL url = new URL(urlApi);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.connect();

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append("\n" + inputLine);
            }
            in.close();
            con.disconnect();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            return new StringBuilder("Error");
        }
    }

}
