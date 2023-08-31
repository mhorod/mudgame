package io.model.input;

import io.model.ScreenPosition;

public record MouseInfo(ScreenPosition position, boolean leftPressed, boolean middlePressed, boolean rightPressed) {
}
