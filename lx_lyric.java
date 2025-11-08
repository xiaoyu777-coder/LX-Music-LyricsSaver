import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class lx_lyric {
    /**
     * Gets the URL for the lyrics API.
     * Prompts the user to enter a port number and validates its validity.
     *
     * @return The constructed URL string.
     */
    public static String getLyricsUrl() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter port (default 23330): ");
        String port = scanner.nextLine();

        if (port.isEmpty()) {
            port = "23330";
            System.out.println("Using default port 23330");
        }
        try {
            int portNumber = Integer.parseInt(port);
            if (portNumber < 1 || portNumber > 65535) {
                System.out.println("Port error, please enter a number between 1-65535.");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Port error, please enter a number between 1-65535.");
            return null;
        }
        return "http://127.0.0.1:" + port + "/lyric";
    }

    /**
     * Fetches lyrics from the specified URL.
     *
     * @param urlStr The URL used to fetch lyrics.
     * @return The fetched lyrics text.
     */
    public static String fetchLyrics(String urlStr) {
        System.out.println("Fetching lyrics...");
        StringBuilder lyrics = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Failed to fetch lyrics, HTTP response code: " + responseCode);
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                lyrics.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error occurred while fetching lyrics: " + e.getMessage());
            return null;
        }

        return lyrics.toString();
    }

    /**
     * Saves the lyrics to a file.
     *
     * @param lyricsText The lyrics content to be saved.
     */
    public static void saveLyricsToFile(String lyricsText) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to save the lyrics? (y/n): ");
        String saveChoice = scanner.nextLine().toLowerCase();

        if (!saveChoice.equals("y")) {
            System.out.println("Save cancelled.");
            return;
        }

        System.out.print("Enter filename (default lyrics.txt): ");
        String filename = scanner.nextLine();
        if (filename.isEmpty()) {
            filename = "lyrics.txt";
            System.out.println("Using default filename lyrics.txt");
        }

        if (!filename.endsWith(".txt")) {
            filename += ".txt";
        }

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(lyricsText);
            System.out.println("Lyrics successfully saved to file: " + filename);
        } catch (IOException e) {
            System.out.println("Error occurred while saving file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String url = getLyricsUrl();
        if (url == null) {
            System.exit(1); // Exit if URL construction fails
        }

        String lyrics = fetchLyrics(url);
        if (lyrics == null) {
            System.exit(1); // Exit if lyrics fetch fails
        }

        System.out.println("\n--- Fetched Lyrics ---\n");
        System.out.println(lyrics);
        System.out.println("\n---------------------\n");

        saveLyricsToFile(lyrics);
    }
}