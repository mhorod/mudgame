package mudgame.server.actions;

import core.entities.events.CreateEntity;
import core.entities.events.MoveEntity;
import core.entities.events.MoveEntityAlongPath;
import core.entities.events.MoveEntityAlongPath.SingleMove;
import core.entities.events.SpawnEntity;
import core.entities.model.Entity;
import mudgame.events.Event;
import mudgame.events.EventOccurrenceObserver;
import core.fogofwar.PlayerFogOfWar;
import core.fogofwar.events.VisibilityChange;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import core.pathfinder.Pathfinder;
import mudgame.server.ServerGameState;
import core.terrain.events.SetTerrain;
import core.terrain.events.SetTerrain.SetPositionTerrain;

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

        VisibilityChange ownerFowEvent = fow.placeEntity(a.owner(), entity, a.position());
        SpawnEntity ownerEvent = new SpawnEntity(entity, a.position(), ownerFowEvent);

        SpawnEntity otherEvent = new SpawnEntity(entity, a.position(), VisibilityChange.empty());

        for (PlayerID player : players())
            if (fow(player).isVisible(a.position())) {
                if (player.equals(entity.owner()))
                    send(ownerEvent, player);
                else
                    send(otherEvent, player);
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
            send(maskedMoves(player, moves, entityID), player);
        }
    }

    private SetTerrain terrainInfo(Set<Position> seenPositions) {
        List<SetPositionTerrain> terrainChanges = new ArrayList<>();
        for (Position p : seenPositions)
            terrainChanges.add(new SetPositionTerrain(p, state.terrain().terrainAt(p)));
        return new SetTerrain(terrainChanges);
    }

    private MoveEntityAlongPath maskedMoves(
            PlayerID player, List<SingleMove> moves, EntityID entityID
    ) {
        List<SingleMove> masked = new ArrayList<>();
        throw new UnsupportedOperationException();
    }

    private List<SingleMove> getMoves(MoveEntity a) {
        List<SingleMove> result = new ArrayList<>();
        PlayerID owner = state.entityBoard().entityOwner(a.entityID());
        List<Position> path = pathfinder.findPath(a.entityID(), a.destination());

        for (Position next : path) {
            VisibilityChange visibilityChange = fow.moveEntity(owner, a.entityID(), next);
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
