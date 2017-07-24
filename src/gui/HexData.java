package gui;

import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/20/2017.
 */
public abstract class HexData extends ModifiableObservableListBase<Byte[]> implements ObservableList<Byte[]>
{
    protected Byte[][] data;

    public HexData(Byte[][] data) { this.data = data; }
    protected HexData() {}

    @Override
    public Byte[] get(int index) { return data[index]; }

    @Override
    public int size() { return data.length; }

    @Override
    protected Byte[] doSet(int index, Byte[] element) {
        Byte[] t = data[index];
        data[index] = element;
        return t;
    }

    // currently unused
    @Override
    protected void doAdd(int index, Byte[] element) {}
    @Override
    protected Byte[] doRemove(int index) { return null; }
}
