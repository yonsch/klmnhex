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

    public DataManager(Byte[][] data) { this.data = data; }

    @Override
    public Byte[] get(int index) { return data[index]; }

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
        Byte[] t = data[index];
        data[index] = element;
        return t;
    }

    @Override
    protected Byte[] doRemove(int index) {
        Byte[] t = data[index];
        Byte[][] newData = new Byte[data.length - 1][data[0].length];
        System.arraycopy(data, 0, newData, 0, index);
        System.arraycopy(data, index + 1, newData, index, data.length - index - 1);
        return t;
    }

    public Byte[][] getData() { return data; }
}
