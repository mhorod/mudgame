package core.entities.model;

import core.entities.components.Component;
import core.entities.components.Vision;

import java.util.List;

public class Entities {
    public static List<Component> playerBase() {
        return List.of(new Vision(10));
    }
}
