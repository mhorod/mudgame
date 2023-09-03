package core.server;

import core.events.Action;
import core.events.EventObserver;
import core.model.PlayerID;
import core.server.rules.ActionRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
final class RuleBasedActionProcessor {
    private final List<ActionRule> rules;
    private final List<EventObserver> observers = new ArrayList<>();

    public void process(Action action, PlayerID actor) {
        log.info("Processing action {} by actor {}", action, actor);
        if (rules.stream().allMatch(rule -> isRuleSatisfied(rule, action, actor)))
            send(action);
    }

    private boolean isRuleSatisfied(ActionRule rule, Action action, PlayerID actor) {
        boolean isSatisfied = rule.isSatisfied(action, actor);
        if (isSatisfied)
            log.info("Rule {} is satisfied", rule.getClass().getSimpleName());
        else
            log.info("Rule {} is not satisfied", rule.getClass().getSimpleName());
        return isSatisfied;
    }

    private void send(Action action) {
        for (EventObserver observer : observers)
            observer.receive(action);
    }

    public void addObserver(EventObserver observer) {
        observers.add(observer);
    }

    public void addObservers(EventObserver... observers) {
        Collections.addAll(this.observers, observers);
    }


}
