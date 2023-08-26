package core.events.model;

import core.model.PlayerID;

import java.util.List;

public record EventOccurrence(Event event, List<PlayerID> recipients) {
}