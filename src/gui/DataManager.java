package gui;

import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/19/2017.
 */
public class DataManager extends ModifiableObservableListBase<Byte[]> implements ObservableList<Byte[]>
{
    private HexData data;

    public DataManager(HexData data) { this.data = data; }

    @Override
    public Byte[] get(int index) { return data.get(index); }

    @Override
    public int size() { return data.getRowCount(); }

    @Override
    protected void doAdd(int index, Byte[] element) {
        // todo
    }

    @Override
    protected Byte[] doSet(int index, Byte[] element) {
        Byte[] t = data.get(index);
        data.set(index, element);
        return t;
    }

    @Override
    protected Byte[] doRemove(int index) {
        //todo
        return null;
    }

    public HexData getData() { return data; }
}
