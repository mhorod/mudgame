package mudgame.server.actions;

import mudgame.events.Event.Action;
import core.model.PlayerID;
import mudgame.server.rules.ActionRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
final class RuleChecker {
    private final List<ActionRule> rules;

    public boolean satisfiesRules(Action action, PlayerID actor) {
        log.info("Processing action {} by actor {}", action, actor);
        return rules.stream().allMatch(rule -> isRuleSatisfied(rule, action, actor));
    }

    private boolean isRuleSatisfied(ActionRule rule, Action action, PlayerID actor) {
        boolean isSatisfied = rule.isSatisfied(action, actor);
        if (isSatisfied)
            log.info("Rule {} is satisfied", rule.getClass().getSimpleName());
        else
            log.info("Rule {} is not satisfied", rule.getClass().getSimpleName());
        return isSatisfied;
    }
}
