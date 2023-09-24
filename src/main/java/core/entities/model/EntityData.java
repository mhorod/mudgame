package core.entities.model;

import core.entities.model.components.Attack;
import core.entities.model.components.Claim;
import core.entities.model.components.Component;
import core.entities.model.components.Cost;
import core.entities.model.components.Health;
import core.entities.model.components.Movement;
import core.entities.model.components.Production;
import core.entities.model.components.Vision;
import core.entities.model.components.visitors.GetAttack;
import core.entities.model.components.visitors.GetCost;
import core.entities.model.components.visitors.GetHealth;
import core.entities.model.components.visitors.GetMovement;
import core.entities.model.components.visitors.GetProduction;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static core.resources.ResourceType.MUD;

public record EntityData(
        EntityType type,
        List<Component> components
) implements Serializable {
    public Optional<Health> getHealth() {
        return new GetHealth().getHealth(components());
    }

    public Optional<Cost> getCost() {
        return new GetCost().getCost(components());
    }

    public Optional<Production> getProduction() {
        return new GetProduction().getProduction(components());
    }

    public Optional<Attack> getAttack() {
        return new GetAttack().getAttack(components());
    }

    public Optional<Movement> getMovement() {
        return new GetMovement().getMovement(components());
    }

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
                        new Health(8),
                        Cost.of(Map.of(MUD, 1))
                )
        );
    }

    public static EntityData warrior() {
        return new EntityData(
                EntityType.WARRIOR,
                List.of(
                        new Vision(2),
                        new Movement(5),
                        new Attack(3, 1, 1),
                        new Health(12),
                        Cost.of(Map.of(MUD, 2))
                )
        );
    }

    public static EntityData marshWiggle() {
        return new EntityData(
                EntityType.MARSH_WIGGLE,
                List.of(
                        new Vision(3),
                        new Movement(6),
                        new Health(10),
                        new Claim(1),
                        Cost.of(Map.of(MUD, 5))
                )
        );
    }

    public static EntityData base() {
        return new EntityData(
                EntityType.BASE,
                List.of(
                        new Claim(2),
                        new Vision(3),
                        new Health(30),
                        Production.of(Map.of(MUD, 2))
                )
        );
    }

    public static EntityData tower() {
        return new EntityData(
                EntityType.TOWER,
                List.of(
                        new Claim(3),
                        new Vision(5),
                        new Health(20),
                        Cost.of(Map.of(MUD, 6)),
                        Production.of(Map.of(MUD, 1))
                )
        );
    }

}
