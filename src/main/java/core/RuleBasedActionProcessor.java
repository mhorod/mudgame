package core;

import core.events.Event.Action;
import core.events.EventObserver;
import core.model.PlayerID;
import core.rules.ActionRule;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class RuleBasedActionProcessor implements ActionProcessor {
    private final List<ActionRule> rules;
    private final List<EventObserver> observers = new ArrayList<>();

    @Override
    public void process(Action action, PlayerID actor) {
        if (rules.stream().allMatch(rule -> rule.isSatisfied(action, actor)))
            send(action);
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
