package io.animation;

import java.util.ArrayList;

public class FutureExecutor {
    private record Subscriber(Finishable finishable, Runnable f) {
    }

    private final ArrayList<Subscriber> subscribers = new ArrayList<>();

    public void update() {
        subscribers.stream()
                .filter(subscriber -> subscriber.finishable.finished())
                .forEach(subscriber -> subscriber.f().run());
        subscribers.removeIf(subscriber -> subscriber.finishable.finished());
    }

    public void onFinish(Finishable finishable, Runnable f) {
        subscribers.add(new Subscriber(finishable, f));
    }
}
