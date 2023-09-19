package middleware.communication;

public interface NetworkDevice {
    void close();

    boolean isClosed();

    void send(Object obj);
}
