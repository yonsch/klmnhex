package gui;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/20/2017.
 */
public interface HexData
{
    int getRowCount();
    int getColumnCount();

    Byte[] get(int row);
    Byte get(int row, int col);

    void set(int row, Byte[] value);
    void set(int row, int col, Byte value);
}
