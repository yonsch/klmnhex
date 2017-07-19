import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.nio.file.Path;


public class HexFile {

    private String path;
    private String fileName;
    private Byte[][] dataArray;
    private int size;


    HexFile(){}
    HexFile(String path){

        byte[] byteArray = readBinaryFile(path);
        size = byteArray.length;
        dataArray = new Byte[(int)Math.ceil(byteArray.length/16.0)][16];


        for (int i = 0; i < byteArray.length; i++) {
            dataArray[i / 16][i % 16] = byteArray[i];
        }
        Path p = Paths.get(path);
        this.fileName = p.getFileName().toString();
        this.path = path;

    }
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

    public void saveAs(String p){
        byte[] b = new byte[size];

        for (int i = 0; i < size; i++) {
            b[i] = dataArray[i / 16][i % 16];
        }
        writeBinaryFile(b, p);
    }
    public void save(){
        saveAs(path);
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Byte[][] getDataArray() {
        return dataArray;
    }

    public void setDataArray(Byte[][] byteArray) {
        this.dataArray = byteArray;
    }
}
