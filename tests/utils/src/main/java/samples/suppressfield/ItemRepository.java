package samples.suppressfield;

import java.util.HashMap;

public class ItemRepository {
    private static HashMap<String, String> itemMap = new HashMap<String, String>();

    @SuppressWarnings("unused")
    private static MyClass myClass = new MyClass();

    private int totalItems = 0;

    public void addItem(String key, String value) {
        itemMap.put(key, value);
        totalItems++;
    }

    public void delItem(String key) {
        if (itemMap.containsKey(key)) {
            itemMap.remove(key);
            totalItems--;
        }
    }

    private static class MyClass {
        public MyClass() {
            throw new IllegalArgumentException("Not possible!");
        }
    }
}