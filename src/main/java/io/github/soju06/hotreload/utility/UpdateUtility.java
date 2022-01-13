package io.github.soju06.hotreload.utility;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateUtility {
    public static String getLatest() throws IOException, ParseException {
        var url = new URL("https://api.github.com/repos/Soju06/hotreload/releases/latest");
        var conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(false);

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line);
            JSONObject obj = (JSONObject) new JSONParser().parse(sb.toString());
            br.close();
            return (String) obj.get("tag_name");
        }
        return null;
    }
}
