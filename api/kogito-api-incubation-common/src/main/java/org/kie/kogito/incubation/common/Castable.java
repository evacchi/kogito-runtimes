package org.kie.kogito.incubation.common;

public interface Castable {
    <T> T as(Class<T> type);
}
