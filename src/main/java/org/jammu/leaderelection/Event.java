package org.jammu.leaderelection;

import java.time.Duration;
import java.util.Objects;

public record Event(Duration time, Runnable runnable) implements Runnable {
    public Event {
        Objects.requireNonNull(time);
        Objects.requireNonNull(runnable);
    }

    @Override
    public void run() {
        runnable.run();
    }
}
