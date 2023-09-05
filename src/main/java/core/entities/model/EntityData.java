package core.entities.model;

import core.entities.components.Attack;
import core.entities.components.Component;
import core.entities.components.Health;
import core.entities.components.Movement;
import core.entities.components.Vision;

import java.io.Serializable;
import java.util.List;

public record EntityData(
        EntityType type,
        List<Component> components
) implements Serializable {
    public static EntityData ofType(EntityType type) {
        return switch (type) {
            case PAWN -> pawn();
            case WARRIOR -> warrior();
            case MARSH_WIGGLE -> marshWiggle();
            case BASE -> base();
            case TOWER -> tower();
        };
    }


    public static EntityData pawn() {
        return new EntityData(
                EntityType.PAWN,
                List.of(
                        new Vision(2),
                        new Movement(8),
                        new Health(8)
                )
        );
    }

    public static EntityData warrior() {
        return new EntityData(
                EntityType.WARRIOR,
                List.of(
                        new Vision(2),
                        new Movement(5),
                        new Attack(3),
                        new Health(12)
                )
        );
    }

    public static EntityData marshWiggle() {
        return new EntityData(
                EntityType.MARSH_WIGGLE,
                List.of(
                        new Vision(3),
                        new Movement(6),
                        new Health(10)
                )
        );
    }

    public static EntityData base() {
        return new EntityData(
                EntityType.BASE,
                List.of(
                        new Vision(3),
                        new Health(30)
                )
        );
    }

    public static EntityData tower() {
        return new EntityData(
                EntityType.TOWER,
                List.of(
                        new Vision(5),
                        new Health(15)
                )
        );
    }

}
