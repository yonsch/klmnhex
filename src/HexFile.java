import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.nio.file.Path;


public class HexFile {
    private ArrayList<Byte> bytes = new ArrayList<>();
    private String path;
    private String fileName;
    private Byte[][] byteArray;


    HexFile(){}
    HexFile(String path){
        byte[] byteArray = readBinaryFile(path);
        for (int i = 0; i < byteArray.length; i++)
            bytes.add(byteArray[i]);
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
    public String toString() {
        //very slow, use only for debugging
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

//    public void open(String path){
//        if(!bytes.isEmpty()){
//            bytes.clear();
//        }
//        byte[] byteArray = readBinaryFile(path);
//        for (int i = 0; i < byteArray.length; i++)
//            bytes.add(byteArray[i]);
//        Path p = Paths.get(path);
//        this.fileName = p.getFileName().toString();
//        this.path = path;
//    }
    public Byte[][] getData(){
        Byte[][] data = new Byte[(int)Math.ceil(bytes.size()/16.0)][16];
        for (int i = 0; i < bytes.size(); i++)
            data[i/16][i%16] = bytes.get(i);
        return data;
    }
    public void saveAs(String p){
        byte[] b = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++)
            b[i] = bytes.get(i);
        writeBinaryFile(b, p);
    }
    public void save(){
        saveAs(path);
    }

    public ArrayList<Byte> getBytes() {
        return bytes;
    }

    public void setBytes(ArrayList<Byte> bytes) {
        this.bytes = bytes;
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

    public Byte[][] getByteArray() {
        return byteArray;
    }

    public void setByteArray(Byte[][] byteArray) {
        this.byteArray = byteArray;
    }
}
