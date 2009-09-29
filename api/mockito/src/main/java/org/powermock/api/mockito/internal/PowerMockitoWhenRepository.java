package org.powermock.api.mockito.internal;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.powermock.core.MockRepository;

public class PowerMockitoWhenRepository {

    public static synchronized void add(Object object, Method method) {
        Map<Object, List<Method>> additionalState = MockRepository.getAdditionalState("replay");
        if (additionalState == null) {
            additionalState = new HashMap<Object, List<Method>>();
            MockRepository.putAdditionalState("replay", additionalState);
        }

        List<Method> methods = additionalState.get(object);
        if (methods == null) {
            methods = new LinkedList<Method>();
            additionalState.put(object, methods);
        }
        methods.add(method);
    }

    public static boolean hasState(Object object, Method method) {
        Map<Object, List<Method>> additionalState = MockRepository.getAdditionalState("replay");
        if (additionalState == null) {
            return false;
        }
        List<Method> list = additionalState.get(object);
        if (list == null || list.isEmpty()) {
            return false;
        }
        return list.contains(method);
    }
}
