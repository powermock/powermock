package samples.powermockito.junit4.bugs.github701;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public final class MapWrapper extends OverridesEquals {
    private final Map<String, Serializable> data;

    public MapWrapper() {
        this(Collections.<String, Serializable>emptyMap());
    }

    MapWrapper(final Map<String, Serializable> data) {
        this.data = Collections.unmodifiableMap(data);
    }

    public Object get(final String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        return data.get(key);
    }
}