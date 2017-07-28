package gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * ಠ^ಠ.
 * Created by Michael on 7/27/2017.
 */
class ModifiableIntegerProperty
{
    private SimpleIntegerProperty i;

    public ModifiableIntegerProperty() { this(0); }
    public ModifiableIntegerProperty(int i) { this.i = new SimpleIntegerProperty(i); }

    public ModifiableIntegerProperty decrease() { i.set(i.get() - 1); return this; }
    public ModifiableIntegerProperty increase() { i.set(i.get() + 1); return this; }
    public ModifiableIntegerProperty add(int n) { i.set(i.get() + n); return this; }
    public ModifiableIntegerProperty sub(int n) { i.set(i.get() - n); return this; }
    public ModifiableIntegerProperty mul(int n) { i.set(i.get() * n); return this; }
    public ModifiableIntegerProperty div(int n) { i.set(i.get() / n); return this; }

    public int get() { return i.get(); }
    public ModifiableIntegerProperty set(int i) { this.i.set(i); return this; }
    public IntegerProperty getProperty() { return i; }
}
