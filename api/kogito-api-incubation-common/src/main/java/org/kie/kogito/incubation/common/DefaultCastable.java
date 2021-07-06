package org.kie.kogito.incubation.common;

public interface DefaultCastable extends Castable {
    default <T> T as(Class<T> type) {
//        return Models.convert(this, type);
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
