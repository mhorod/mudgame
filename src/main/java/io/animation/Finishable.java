package io.animation;

import java.util.Arrays;
import java.util.List;

public interface Finishable {
    boolean finished();

    static Finishable all(Finishable... finishables) {
        return () -> Arrays.stream(finishables).allMatch(Finishable::finished);
    }

    static Finishable any(Finishable... finishables) {
        return () -> Arrays.stream(finishables).anyMatch(Finishable::finished);
    }

    static Finishable all(List<Finishable> finishables) {
        return () -> finishables.stream().allMatch(Finishable::finished);
    }

    static Finishable any(List<Finishable> finishables) {
        return () -> finishables.stream().anyMatch(Finishable::finished);
    }
}
