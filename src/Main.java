import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Michael on 7/18/2017.
 */
public class Main
{
    static byte[] readBinaryFile(String filename) {
        try { return Files.readAllBytes(Paths.get(filename)); }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error: Could Not Read " + filename);
        }
    }

    static void writeBinaryFile(byte[] bytes, String filename) {
        try { Files.write(Paths.get(filename), bytes); }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error: Could Not Write to " + filename);
        }
    }

    public static void main(String[] args) {
        byte[] data = readBinaryFile("texture.png");
    }
}
