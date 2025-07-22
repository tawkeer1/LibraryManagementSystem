package library.example.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenericAssetManager<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L; // ✅ Required for Serializable class

    private final List<T> items = Collections.synchronizedList(new ArrayList<>());

    public void add(T item) {
        items.add(item);
    }

    public void remove(T item) {
        items.remove(item);
    }

    public List<T> getAll() {
        return new ArrayList<>(items);
    }

    public boolean contains(T item) {
        return items.contains(item);
    }

    public void clear() {
        items.clear();
    }
}
