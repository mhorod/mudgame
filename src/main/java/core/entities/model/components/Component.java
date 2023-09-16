package core.entities.model.components;

import java.io.Serializable;

public sealed interface Component extends Serializable
        permits
        Attack, Claim, Cost, Health, Movement, Production, Vision {
    <T> T accept(ComponentVisitor<T> visitor);
}
