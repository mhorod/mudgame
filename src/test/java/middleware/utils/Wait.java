package middleware.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.exception.UncheckedException;

import java.time.Duration;

@UtilityClass
public class Wait {
    public static final Duration VERIFY_WAIT = Duration.ofMillis(100);
    public static final Duration EPS = Duration.ofMillis(3);

    public void verify_wait() {
        try {
            Thread.sleep(VERIFY_WAIT.toMillis());
        } catch (InterruptedException e) {
            throw new UncheckedException(e);
        }
    }
}
