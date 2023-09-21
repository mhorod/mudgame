package io;

import core.claiming.ClaimChange;
import core.terrain.model.TerrainType;
import io.game.world.Map;
import mudgame.controls.events.*;
import mudgame.controls.events.VisibilityChange.HidePosition;
import mudgame.controls.events.VisibilityChange.ShowPosition;
import org.junit.jupiter.api.Test;
import testutils.integration.utils.RectangleTerrain;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static testutils.Actions.move;
import static testutils.Entities.*;
import static testutils.Players.PLAYER_0;
import static testutils.Players.PLAYER_1;
import static testutils.Positions.pos;
import static testutils.integration.scenarios.Scenarios.two_players;

public class AnimationTest {

    static class EventEater implements Consumer<Event> {
        List<Event> events = new ArrayList<>();
        final Consumer<Event> f;

        EventEater(Consumer<Event> f) {
            this.f = f;
        }

        @Override
        public void accept(Event event) {
            events.add(event);
            this.f.accept(event);
        }
    }

    @Test
    void move_entity_animation_finishes_gracefully() {
        var pawn = pawn(PLAYER_0);
        var scenario = two_players()
                .with(RectangleTerrain.land(10, 10))
                .with(base(PLAYER_1), pos(0, 0))
                .with(pawn, pos(3, 3))
                .build();

        var core = scenario.clientCore(PLAYER_1);
        Map map = new Map(core.terrain(), core.entityBoard(), core.claimedArea());
        var eater = new EventEater(event -> {
            assertThat(event).isInstanceOf(MoveEntityAlongPath.class);
            map.animate((MoveEntityAlongPath) event);
        });
        scenario.setBeforeApply(PLAYER_1, eater);

        scenario.act(PLAYER_0, move(pawn, pos(2, 2)));

        assertThat(eater.events).hasSize(1);
        assertThat(map.getAnimatedEntities()).isNotEmpty();
        for (int i = 0; i < 1000; i++) {
            map.update(1f / 60);
        }
        assertThat(map.getAnimatedTiles()).isEmpty();
        assertThat(map.getAnimatedEntities()).isEmpty();
    }

    @Test
    void attack_animation_finishes_gracefully() {
        var warrior = warrior(PLAYER_0);
        var base = base(PLAYER_1);
        var scenario = two_players()
                .with(RectangleTerrain.land(10, 10))
                .with(base, pos(0, 0))
                .with(warrior, pos(1, 0))
                .build();

        var core = scenario.clientCore(PLAYER_1);
        Map map = new Map(core.terrain(), core.entityBoard(), core.claimedArea());

        var event = new AttackEntityEvent(warrior.id(), base.id(), 10);

        var finishable = map.animate(event);
        core.receive(event);

        assertThat(finishable.finished()).isFalse();
        assertThat(map.getAnimatedEntities()).isNotEmpty();
        for (int i = 0; i < 1000; i++) {
            map.update(1f / 60);
            if (finishable.finished()) {
                assertThat(i).isGreaterThan(0);
                assertThat(map.getAnimatedTiles()).isEmpty();
                assertThat(map.getAnimatedEntities()).isEmpty();
                return;
            }
        }
        fail();
    }

    @Test
    void kill_animation_finishes_gracefully() {
        // given
        var base = base(PLAYER_0);
        var scenario = two_players()
                .with(RectangleTerrain.land(10, 10))
                .with(base, pos(0, 0))
                .build();

        var core = scenario.clientCore(PLAYER_0);
        Map map = new Map(core.terrain(), core.entityBoard(), core.claimedArea());

        // when
        var event = new KillEntity(base.id(), new VisibilityChange(List.of()), new ClaimChange(List.of(), List.of()));
        var finishable = map.animate(event);
        core.receive(event);

        // then
        assertThat(finishable.finished()).isFalse();
        assertThat(map.getAnimatedEntities()).isNotEmpty();
        for (int i = 0; i < 1000; i++) {
            map.update(1f / 60);
            if (finishable.finished()) {
                assertThat(i).isGreaterThan(0);
                assertThat(map.getAnimatedTiles()).isEmpty();
                assertThat(map.getAnimatedEntities()).isEmpty();
                return;
            }
        }
        fail();
    }

    @Test
    void visibility_change_animation_finishes_gracefully() {
        // given
        var base = base(PLAYER_0);
        var scenario = two_players()
                .with(RectangleTerrain.land(10, 10))
                .with(base, pos(0, 0))
                .build();

        var core = scenario.clientCore(PLAYER_0);
        Map map = new Map(core.terrain(), core.entityBoard(), core.claimedArea());

        // when
        var event = new VisibilityChange(List.of(
                new HidePosition(pos(0, 1)),
                new ShowPosition(pos(7, 0), TerrainType.LAND, List.of(base(PLAYER_1)), PLAYER_0)
        ));
        var finishable = map.animate(event);
        core.receive(event);

        // then
        assertThat(finishable.finished()).isFalse();
        for (int i = 0; i < 1000; i++) {
            map.update(1f / 60);
            if (finishable.finished()) {
                assertThat(i).isGreaterThan(0);
                assertThat(map.getAnimatedTiles()).isEmpty();
                assertThat(map.getAnimatedEntities()).isEmpty();
                return;
            }
        }
        fail();
    }

    @Test
    void spawn_animation_finishes_gracefully() {
        // given
        var base = base(PLAYER_0);
        var scenario = two_players()
                .with(RectangleTerrain.land(10, 10))
                .with(base, pos(0, 0))
                .build();

        var core = scenario.clientCore(PLAYER_0);
        Map map = new Map(core.terrain(), core.entityBoard(), core.claimedArea());

        // when
        var event = new SpawnEntity(warrior(PLAYER_1), pos(1, 1), new VisibilityChange(List.of()), new ClaimChange(List.of(), List.of()));
        var finishable = map.animate(event);
        core.receive(event);

        // then
        assertThat(finishable.finished()).isFalse();
        for (int i = 0; i < 1000; i++) {
            map.update(1f / 60);
            if (finishable.finished()) {
                assertThat(i).isGreaterThan(0);
                assertThat(map.getAnimatedTiles()).isEmpty();
                assertThat(map.getAnimatedEntities()).isEmpty();
                return;
            }
        }
        fail();
    }

    @Test
    void remove_entity_animation_finishes_gracefully() {
        // given
        var base = base(PLAYER_0);
        var scenario = two_players()
                .with(RectangleTerrain.land(10, 10))
                .with(base, pos(0, 0))
                .build();

        var core = scenario.clientCore(PLAYER_0);
        Map map = new Map(core.terrain(), core.entityBoard(), core.claimedArea());

        // when
        var event = new RemoveEntity(base.id());
        var finishable = map.animate(event);
        core.receive(event);

        // then
        assertThat(finishable.finished()).isFalse();
        for (int i = 0; i < 1000; i++) {
            map.update(1f / 60);
            if (finishable.finished()) {
                assertThat(i).isGreaterThan(0);
                assertThat(map.getAnimatedTiles()).isEmpty();
                assertThat(map.getAnimatedEntities()).isEmpty();
                return;
            }
        }
        fail();
    }
}
