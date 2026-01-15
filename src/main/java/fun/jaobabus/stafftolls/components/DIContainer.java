package fun.jaobabus.stafftolls.components;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DIContainer
{
    private final Set<Class<?>> incomplete = new HashSet<>();
    private final Map<Class<?>, BaseComponent> instances = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends BaseComponent> T get(Class<T> clazz)
    {
        var v = (T) instances.get(clazz);
        if (v == null) {
            v = construct(clazz);
            instances.put(clazz, v);
        }
        return v;
    }

    public <T extends BaseComponent> T construct(Class<T> clazz) {
        try {
            if (incomplete.contains(clazz)) {
                throw new RuntimeException("Cyclic init: " + String.join(" → ",
                        incomplete.stream().map(Class::getSimpleName).toList()) + " → " + clazz.getSimpleName());
            }

            incomplete.add(clazz);
            return clazz.getDeclaredConstructor(DIContainer.class).newInstance(this);
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Error initializing " + clazz, e);
        }
        finally {
            incomplete.remove(clazz);
        }
    }

}
