package mudgame.server.actions;

import core.model.PlayerID;
import mudgame.events.Event.Action;
import mudgame.server.rules.ActionRule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RuleCheckerTest {
    @Test
    void returns_true_for_one_satisfied_rule() {
        // given
        ActionRule rule = mock(ActionRule.class);
        when(rule.isSatisfied(any(), any())).thenReturn(true);
        List<ActionRule> rules = List.of(rule);
        RuleChecker ruleChecker = new RuleChecker(rules);

        // when
        Action action = mock(Action.class);
        PlayerID actor = new PlayerID(0);

        // then
        assertThat(ruleChecker.satisfiesRules(action, actor)).isTrue();
    }

    @Test
    void returns_false_when_there_is_unsatisfied_rule() {
        // given
        ActionRule satisfiedRule = mock(ActionRule.class);
        when(satisfiedRule.isSatisfied(any(), any())).thenReturn(true);

        ActionRule unsatisfiedRule = mock(ActionRule.class);
        when(unsatisfiedRule.isSatisfied(any(), any())).thenReturn(false);


        List<ActionRule> rules = List.of(satisfiedRule, unsatisfiedRule);
        RuleChecker ruleChecker = new RuleChecker(rules);

        // when
        Action action = mock(Action.class);
        PlayerID actor = new PlayerID(0);

        // then
        assertThat(ruleChecker.satisfiesRules(action, actor)).isFalse();
    }

    @Test
    void returns_true_when_there_are_no_rules() {
        // given
        List<ActionRule> rules = List.of();
        RuleChecker ruleChecker = new RuleChecker(rules);

        // when
        Action action = mock(Action.class);
        PlayerID actor = new PlayerID(0);

        // then
        assertThat(ruleChecker.satisfiesRules(action, actor)).isTrue();
    }
}