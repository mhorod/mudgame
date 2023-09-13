package core.resources;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@ToString
@EqualsAndHashCode
public class Resources implements Serializable {
    private final Map<ResourceType, Integer> amount;

    private Resources(Map<ResourceType, Integer> amount) {
        this.amount = new EnumMap<>(amount);
        Arrays.stream(ResourceType.values())
                .forEach(r -> this.amount.computeIfAbsent(r, k -> 0));

    }

    public Resources() {
        this.amount = Arrays.stream(ResourceType.values()).collect(toMap(r -> r, r -> 0));
    }

    public static Resources of(Map<ResourceType, Integer> amount) {
        return new Resources(amount);
    }

    public boolean isGreaterThanOrEqual(Resources cost) {
        return cost.amount.entrySet()
                .stream()
                .allMatch(e -> amount.get(e.getKey()) >= e.getValue());
    }

    public void add(Resources resources) {
        resources.amount.forEach(this::addResource);
    }

    private void addResource(ResourceType type, int addAmount) {
        amount.put(type, amount.getOrDefault(type, 0) + addAmount);
    }

    public void subtract(Resources resources) {
        resources.amount.forEach(this::subtractResource);
    }

    private void subtractResource(ResourceType type, int subtractAmount) {
        amount.put(type, amount.getOrDefault(type, 0) - subtractAmount);
    }

    public void set(ResourceType type, int amount) {
        this.amount.put(type, amount);
    }
}
