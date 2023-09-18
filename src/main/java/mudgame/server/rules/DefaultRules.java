package mudgame.server.rules;

import core.gameover.GameOverCondition;
import core.pathfinder.Pathfinder;
import core.spawning.SpawnManager;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.controls.actions.MoveEntity;
import mudgame.server.rules.attack.AttackedEntityIsInAttackRange;
import mudgame.server.rules.attack.AttackerSeesAttackedEntity;
import mudgame.server.rules.attack.PlayerCannotAttackOwnEntities;
import mudgame.server.rules.attack.PlayerOwnsAttackerEntity;
import mudgame.server.rules.creation.PlayerCanCreateEntity;
import mudgame.server.rules.movement.MoveDestinationIsEmpty;
import mudgame.server.rules.movement.MoveDestinationIsLand;
import mudgame.server.rules.movement.MoveDestinationIsReachable;
import mudgame.server.rules.movement.PlayerOwnsMovedEntity;
import mudgame.server.rules.movement.PlayerSeesMoveDestination;
import mudgame.server.rules.turn.GameIsNotOver;
import mudgame.server.rules.turn.PlayerTakesActionDuringOwnTurn;
import mudgame.server.state.ServerGameState;

import java.util.List;

import static mudgame.server.rules.RuleGroup.groupRules;

public class DefaultRules implements RuleProvider {
    @Override
    public List<ActionRule> rules(
            ServerGameState gameState,
            GameOverCondition gameOverCondition
    ) {
        Pathfinder pathfinder = gameState.pathfinder();
        SpawnManager spawnManager = gameState.spawnManager();

        return List.of(
                // turn rules
                new PlayerTakesActionDuringOwnTurn(gameState.turnManager()),
                new GameIsNotOver(gameOverCondition),

                // entity creation rules
                new PlayerCanCreateEntity(spawnManager),

                // entity movement rules
                groupRules(
                        new PlayerOwnsMovedEntity(gameState.entityBoard()),
                        new PlayerSeesMoveDestination(gameState.fogOfWar()),
                        new MoveDestinationIsEmpty(gameState.entityBoard()),
                        new MoveDestinationIsLand(gameState.terrain()),
                        new MoveDestinationIsReachable(pathfinder)
                ).forActions(MoveEntity.class),

                // attack rules
                groupRules(
                        new AttackerSeesAttackedEntity(gameState.entityBoard(),
                                                       gameState.fogOfWar()),
                        new PlayerOwnsAttackerEntity(gameState.entityBoard()),
                        new PlayerCannotAttackOwnEntities(gameState.entityBoard()),
                        new AttackedEntityIsInAttackRange(gameState.entityBoard())
                ).forActions(AttackEntityAction.class)
        );
    }
}
