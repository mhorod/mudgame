package middleware.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.time.Duration;

@UtilityClass
public class Wait {
    public static final Duration VERIFY_WAIT = Duration.ofMillis(100);
    public static final Duration EPS = Duration.ofMillis(3);

    @SneakyThrows
    public static void verify_wait() {
        Thread.sleep(VERIFY_WAIT.toMillis());
    }
}
