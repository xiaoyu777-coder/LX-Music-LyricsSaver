import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class lx_lyric {
    /**
     * 获取歌词 API 的 URL。
     * 提示用户输入端口号并验证其有效性。
     *
     * @return 构建好的 URL 字符串。
     */
    public static String getLyricsUrl() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入端口(默认23330): ");
        String port = scanner.nextLine();

        if (port.isEmpty()) {
            port = "23330";
            System.out.println("使用默认端口 23330");
        }
        try {
            int portNumber = Integer.parseInt(port);
            if (portNumber < 1 || portNumber > 65535) {
                System.out.println("端口错误，请输入1-65535之间的数字。");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("端口错误，请输入1-65535之间的数字。");
            return null;
        }
        return "http://127.0.0.1:" + port + "/lyric";
    }

    /**
     * 从指定的 URL 获取歌词。
     *
     * @param urlStr 用于获取歌词的 URL。
     * @return 获取到的歌词文本。
     */
    public static String fetchLyrics(String urlStr) {
        System.out.println("正在获取歌词...");
        StringBuilder lyrics = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("获取歌词失败，HTTP 响应码: " + responseCode);
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                lyrics.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("获取歌词时发生错误: " + e.getMessage());
            return null;
        }

        return lyrics.toString();
    }

    /**
     * 将歌词保存到文件。
     *
     * @param lyricsText 需要保存的歌词内容。
     */
    public static void saveLyricsToFile(String lyricsText) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("是否要保存歌词？(y/n): ");
        String saveChoice = scanner.nextLine().toLowerCase();

        if (!saveChoice.equals("y")) {
            System.out.println("已取消保存。");
            return;
        }

        System.out.print("请输入文件名(默认lyrics.txt): ");
        String filename = scanner.nextLine();
        if (filename.isEmpty()) {
            filename = "lyrics.txt";
            System.out.println("使用默认文件名 lyrics.txt");
        }

        if (!filename.endsWith(".txt")) {
            filename += ".txt";
        }

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(lyricsText);
            System.out.println("歌词已成功保存到文件: " + filename);
        } catch (IOException e) {
            System.out.println("保存文件时出错: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String url = getLyricsUrl();
        if (url == null) {
            System.exit(1); // 如果 URL 构建失败则退出
        }

        String lyrics = fetchLyrics(url);
        if (lyrics == null) {
            System.exit(1); // 如果歌词获取失败则退出
        }

        System.out.println("\n--- 获取到的歌词 ---\n");
        System.out.println(lyrics);
        System.out.println("\n---------------------\n");

        saveLyricsToFile(lyrics);
    }
}
