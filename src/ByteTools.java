import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ByteTools {
    public static float bytesToFloat(byte[] bytes, boolean big){
        if (big) return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getFloat();
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }
    public static double bytesToDouble(byte[] bytes, boolean big){
        if (big) return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getDouble();
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getDouble();
    }
    public static int bytesToInt(byte[] bytes, boolean big){
        if (big) return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getInt();
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    public static long bytesToLong(byte[] bytes, boolean big){
        if (big) return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getLong();
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }
    public static long bytesToShort(byte[] bytes, boolean big){
        if (big) return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getShort();
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }
    public static byte[] readBinaryFile(String filename) {
        try { return Files.readAllBytes(Paths.get(filename)); }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error: Could Not Read " + filename);
        }
    }
    public static void writeBinaryFile(byte[] bytes, String filename) {
        try { Files.write(Paths.get(filename), bytes); }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error: Could Not Write to " + filename);
        }
    }
}
