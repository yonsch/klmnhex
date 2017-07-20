import gui.HexData;

import java.nio.file.Paths;

public class HexFile implements HexData
{
    private String path;
    private String fileName;
    private Byte[][] data;
    private int size;

    HexFile(String path) {
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

    @Override
    public int getRowCount() { return data.length; }

    @Override
    public int getColumnCount() { return data.length > 0 ? data[0].length : 0; }

    @Override
    public Byte[] get(int row) { return data[row]; }

    @Override
    public Byte get(int row, int col) { return data[row][col]; }

    @Override
    public void set(int row, Byte[] value) { data[row] = value; }

    @Override
    public void set(int row, int col, Byte value) { data[row][col] = value; }
}
