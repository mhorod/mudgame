package mudgame.server.rules;

import core.event.Action;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Slf4j
public final class RuleGroup implements ActionRule, Serializable {
    private final List<ActionRule> rules;
    private final List<Class<? extends Action>> actionTypes;

    private RuleGroup(
            List<ActionRule> rules,
            List<Class<? extends Action>> actionTypes
    ) {
        this.rules = rules;
        this.actionTypes = actionTypes;
    }

    public static RuleGroupBuilder groupRules(ActionRule... rules) {
        return new RuleGroupBuilder(List.of(rules));
    }

    @RequiredArgsConstructor
    public final static class RuleGroupBuilder {
        private final List<ActionRule> rules;

        @SafeVarargs
        public final RuleGroup forActions(Class<? extends Action>... actionTypes) {
            return new RuleGroup(rules, List.of(actionTypes));
        }
    }

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (actionTypes.contains(action.getClass()))
            return rules.stream().allMatch(rule -> isRuleSatisfied(rule, action, actor));
        else
            return true;
    }

    private boolean isRuleSatisfied(ActionRule rule, Action action, PlayerID actor) {
        boolean isSatisfied = rule.isSatisfied(action, actor);
        if (isSatisfied)
            log.info("Rule {} is satisfied", rule.getClass().getSimpleName());
        else
            log.info("Rule {} is not satisfied", rule.getClass().getSimpleName());
        return isSatisfied;
    }

    @Override
    public String name() {
        List<String> actionTypeNames = actionTypes.stream().map(Class::getSimpleName).toList();
        return String.format("RuleGroup[actionTypes=%s]", actionTypeNames);
    }
}
