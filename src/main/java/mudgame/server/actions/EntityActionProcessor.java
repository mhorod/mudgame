package mudgame.server.actions;

import core.entities.model.Entity;
import core.event.Event;
import core.fogofwar.PlayerFogOfWar;
import core.fogofwar.PlayerFogOfWar.PositionVisibility;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import core.pathfinder.Pathfinder;
import mudgame.controls.actions.CreateEntity;
import mudgame.controls.actions.MoveEntity;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.MoveEntityAlongPath.SingleMove;
import mudgame.controls.events.SpawnEntity;
import mudgame.controls.events.VisibilityChange;
import mudgame.controls.events.VisibilityChange.HidePosition;
import mudgame.controls.events.VisibilityChange.PositionVisibilityChange;
import mudgame.controls.events.VisibilityChange.ShowPosition;
import mudgame.events.EventOccurrenceObserver;
import mudgame.server.ServerGameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class EntityActionProcessor {

    private final ServerGameState state;
    private final EventOccurrenceObserver eventOccurrenceObserver;
    private final EventFogOfWar fow;
    private final Pathfinder pathfinder;

    EntityActionProcessor(
            ServerGameState state, EventOccurrenceObserver eventOccurrenceObserver
    ) {
        this.state = state;
        this.eventOccurrenceObserver = eventOccurrenceObserver;
        pathfinder = new Pathfinder(
                state.terrain(),
                state.entityBoard()
        );
        fow = new EventFogOfWar(state.fogOfWar());
    }

    public void createEntity(CreateEntity a) {
        Entity entity = state
                .entityBoard()
                .createEntity(a.type(), a.owner(), a.position());

        VisibilityChange ownerVisibilityChange = visibilityChange(
                fow.placeEntity(a.owner(), entity, a.position()));
        SpawnEntity ownerEvent = new SpawnEntity(entity, a.position(), ownerVisibilityChange);

        SpawnEntity otherEvent = new SpawnEntity(entity, a.position(),
                                                 VisibilityChange.empty());

        for (PlayerID player : players())
            if (fow(player).isVisible(a.position())) {
                if (player.equals(entity.owner()))
                    send(ownerEvent, player);
                else
                    send(otherEvent, player);
            }
    }

    private VisibilityChange visibilityChange(Set<PositionVisibility> positions) {
        return new VisibilityChange(
                positions.stream()
                        .filter(p -> state.terrain().contains(p.position()))
                        .map(this::positionVisibilityChange).toList()
        );
    }

    private PositionVisibilityChange positionVisibilityChange(PositionVisibility pv) {
        if (!pv.isVisible())
            return new HidePosition(pv.position());
        else {
            return new ShowPosition(
                    pv.position(),
                    state.terrain().terrainAt(pv.position()),
                    state.entityBoard().entitiesAt(pv.position())
            );
        }

    }

    private void send(Event event, PlayerID player) {
        eventOccurrenceObserver.receive(event, player);
    }


    public void moveEntity(MoveEntity a) {
        List<SingleMove> moves = getMoves(a);
        for (PlayerID player : players())
            sendMovesTo(player, moves, a.entityID());
    }

    private void sendMovesTo(PlayerID player, List<SingleMove> moves, EntityID entityID) {
        PlayerID owner = state.entityBoard().entityOwner(entityID);
        if (player.equals(owner)) {
            send(new MoveEntityAlongPath(entityID, moves), player);
        } else {
            send(new MoveEntityAlongPath(entityID, maskedMoves(player, moves, entityID)), player);
        }
    }

    private List<SingleMove> maskedMoves(
            PlayerID player, List<SingleMove> moves, EntityID entityID
    ) {
        List<SingleMove> masked = new ArrayList<>();

        return masked;
    }

    private List<SingleMove> getMoves(MoveEntity a) {
        List<SingleMove> result = new ArrayList<>();
        PlayerID owner = state.entityBoard().entityOwner(a.entityID());
        List<Position> path = pathfinder.findPath(a.entityID(), a.destination());

        for (Position next : path) {
            VisibilityChange visibilityChange = visibilityChange(
                    fow.moveEntity(owner, a.entityID(), next));
            state.entityBoard().moveEntity(a.entityID(), next);
            result.add(new SingleMove(Optional.of(next), visibilityChange));
        }
        return result;
    }

    private PlayerFogOfWar fow(PlayerID playerID) {
        return state.fogOfWar().playerFogOfWar(playerID);
    }

    private List<PlayerID> players() {
        return state.playerManager().getPlayerIDs();
    }
}
