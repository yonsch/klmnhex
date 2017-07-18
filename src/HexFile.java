import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.nio.file.Path;

/**
 * Created by Yonch on 18/07/2017.
 */
public class HexFile {
    ArrayList<Byte> bytes = new ArrayList<>();
    String path;
    String fileName;

    HexFile(){}

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
    public String toString() {
        String result = "a hex file at: " + path;
        for (int i = 0; i < bytes.size(); i++) {
            if(i%16==0){
                result += "\n";
            }
            result += bytes.get(i).toString();
            result += " ";

        }
        return result;
    }

    public void open(String path){
        //c:/users/yonch/documents/mesh.stl
        byte[] byteArray = readBinaryFile(path);
        for (int i = 0; i < byteArray.length; i++) {
            bytes.add(byteArray[i]);
        }
        Path p = Paths.get(path);
        this.fileName = p.getFileName().toString();
        this.path = path;


    }
    public void saveAs(String p){
        byte[] b = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            b[i] = bytes.get(i);
        }
        writeBinaryFile(b, p);
    }
    public void save(){
        saveAs(path);
    }

}
