package core.server;

import core.events.Event.Action;
import core.events.EventObserver;
import core.model.PlayerID;
import core.server.rules.ActionRule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RuleBasedActionProcessorTest {
    @Test
    void action_is_sent_when_all_rules_are_satisfied() {
        // given
        ActionRule rule = mock(ActionRule.class);
        when(rule.isSatisfied(any(), any())).thenReturn(true);
        List<ActionRule> rules = List.of(rule);
        RuleBasedActionProcessor actionProcessor = new RuleBasedActionProcessor(rules);

        EventObserver observer = mock(EventObserver.class);
        actionProcessor.addObserver(observer);

        // when
        Action action = mock(Action.class);
        PlayerID actor = new PlayerID(0);
        actionProcessor.process(action, actor);

        // then
        verify(observer).receive(action);
    }

    @Test
    void action_is_sent_when_there_is_unsatisfied_rule() {
        // given
        ActionRule satisfiedRule = mock(ActionRule.class);
        when(satisfiedRule.isSatisfied(any(), any())).thenReturn(true);

        ActionRule unsatisfiedRule = mock(ActionRule.class);
        when(unsatisfiedRule.isSatisfied(any(), any())).thenReturn(false);


        List<ActionRule> rules = List.of(satisfiedRule, unsatisfiedRule);
        RuleBasedActionProcessor actionProcessor = new RuleBasedActionProcessor(rules);

        EventObserver observer = mock(EventObserver.class);
        actionProcessor.addObserver(observer);

        // when
        Action action = mock(Action.class);
        PlayerID actor = new PlayerID(0);
        actionProcessor.process(action, actor);

        // then
        verifyNoInteractions(observer);
    }

    @Test
    void action_is_sent_when_there_are_no_rules() {
        // given
        List<ActionRule> rules = List.of();
        RuleBasedActionProcessor actionProcessor = new RuleBasedActionProcessor(rules);

        EventObserver observer = mock(EventObserver.class);
        actionProcessor.addObserver(observer);

        // when
        Action action = mock(Action.class);
        PlayerID actor = new PlayerID(0);
        actionProcessor.process(action, actor);

        // then
        verify(observer).receive(action);
    }
}