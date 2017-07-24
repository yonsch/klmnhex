import gui.table.HexData;

import java.nio.file.Paths;

public class HexFile extends HexData
{
    private String path;
    private String fileName;
    private int size;

    public HexFile(String path) {
        byte[] byteArray = ByteTools.readBinaryFile(path);
        size = byteArray.length;

        data = new Byte[(int) Math.ceil(byteArray.length / 16.0)][16];
        for (int i = 0; i < byteArray.length; i++) data[i / 16][i % 16] = byteArray[i];

        this.fileName = Paths.get(path).getFileName().toString();
        this.path = path;
    }

    public void saveAs(String p) {
        byte[] b = new byte[size];
        for (int i = 0; i < size; i++) b[i] = data[i / 16][i % 16];

        ByteTools.writeBinaryFile(b, p);
    }
    public void save() { saveAs(path); }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Byte[][] getData() { return data; }
    public void setData(Byte[][] byteArray) { this.data = byteArray; }
}
