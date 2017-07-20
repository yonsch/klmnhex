package gui;

import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/19/2017.
 */
public class DataManager extends ModifiableObservableListBase<Byte[]> implements ObservableList<Byte[]>
{
    private Byte[][] data;
    private Byte[] header;

    public DataManager(Byte[][] data) {
        this.data = data;
        header = new Byte[data[0].length];
        for (byte i = 0; i < data[0].length; i++) header[i] = i;
    }

    @Override
    public Byte[] get(int index) {
        if (index == 0) return header;
        return data[index - 1];
    }

    @Override
    public int size() { return data.length; }

    @Override
    protected void doAdd(int index, Byte[] element) {
        Byte[][] newData = new Byte[data.length + 1][data[0].length];
        System.arraycopy(data, 0, newData, 0, data.length);
        newData[data.length] = element;
        data = newData;
    }

    @Override
    protected Byte[] doSet(int index, Byte[] element) {
        Byte[] t = data[index - 1];
        data[index - 1] = element;
        return t;
    }

    @Override
    protected Byte[] doRemove(int index) {
        Byte[] t = data[index - 1];
        Byte[][] newData = new Byte[data.length - 1][data[0].length];
        System.arraycopy(data, 0, newData, 0, index - 1);
        System.arraycopy(data, index, newData, index - 1, data.length - index);
        return t;
    }

    public Byte[][] getData() { return data; }

    public Byte[] getHeader() { return header; }
}
