package middleware.model;

import java.io.Serializable;

public record UserID(long id) implements Serializable {
    public static final String DEFAULT_NAME = "MudMan";
}
