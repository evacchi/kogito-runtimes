package org.kie.kogito.incubation.common;

/**
 * A DataContext that behaves like a Map.
 */
public interface MapLikeDataContext extends DataContext {
    void set(String key, Object value);
    Object get(String key);
    <T> T get(String key, Class<T> expectedType);
}
