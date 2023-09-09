package core.entities.components;

import java.io.Serializable;

public sealed interface Component extends Serializable
        permits
        Attack, Claim, Health, Movement, Vision {
    <T> T accept(ComponentVisitor<T> visitor);
}
