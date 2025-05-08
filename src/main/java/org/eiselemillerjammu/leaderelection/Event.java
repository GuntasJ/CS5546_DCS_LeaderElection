package org.eiselemillerjammu.leaderelection;

import java.time.Duration;
import java.util.Objects;

/**
 * A record that represents an event to be called.
 * @param time The time at which this event should be called.
 * Implementation wise, this event will be called whenever it has the least time in the {@code eventQueue}.
 * After it has been called, the {@code Simulator currentTime} is changed to equal this time.
 * @param runnable The no-arg function to call once for this event
 */
public record Event(Duration time, Runnable runnable) implements Runnable {
    public Event {
        Objects.requireNonNull(time);
        Objects.requireNonNull(runnable);
    }

    /**
     * A wrapper function that calls the no-arg function passed in the Event
     */
    @Override
    public void run() {
        runnable.run();
    }
}
