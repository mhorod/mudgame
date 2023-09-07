package middleware.communication;

public interface NetworkDevice {
    void scheduleToClose();

    void close();

    boolean isClosedOrScheduledToClose();

    boolean isClosed();
}
