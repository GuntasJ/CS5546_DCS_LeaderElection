package org.jammu.eventprocessing;

import java.time.Duration;
import java.util.function.Consumer;

public record Event<T>(Duration time, T input, Consumer<T> consumer) {

}
