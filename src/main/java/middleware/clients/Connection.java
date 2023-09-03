package middleware.clients;

public interface Connection<T extends NetworkClient<T>> {
    void connect(T client);
}
