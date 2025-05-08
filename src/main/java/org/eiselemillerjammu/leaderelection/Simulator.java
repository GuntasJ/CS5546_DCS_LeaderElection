package org.eiselemillerjammu.leaderelection;

import java.time.Duration;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SplittableRandom;

/**
 * A class that handles the {@code events} that are created by the {@code nodes}.
 * This class is responsible for adding the {@code events} in a {@link Queue}, as well as calling and deleting them
 */
public final class Simulator implements Runnable {
    /**
     * The max latency, in ms, that can occur when adding an event
     */
    private static final int MAX_LATENCY = 100;

    /**
     * The queue that holds the events. This is to be a {@link PriorityQueue}
     */
    private final Queue<Event> eventQueue;

    /**
     * A {@link SplittableRandom} that is used to generate random times for the delays to be added to each {@link Event}
     */
    private final SplittableRandom random;

    /**
     * The current time that this {@link Simulator} is at. This starts from zero, and it is changed after each
     * {@link Event} is called.
     */
    private Duration currentTime;

    /**
     * Initialize fields with default values.
     * Values {@code events} are compared with their
     * time, i.e., the earliest events are called before the later ones
     */
    public Simulator() {
        eventQueue = new PriorityQueue<>(Comparator.comparing(Event::time));
        random = new SplittableRandom(0);
        currentTime = Duration.ZERO;
    }

    /**
     * @return The current time of the simulation
     */
    public Duration getCurrentTime() {
        return currentTime;
    }

    /**
     * Adds an event to the {@link Simulator#eventQueue}, with the same information as the event passed, save a delay
     * added on to the new event
     * @param event The event to be added
     */
    public void addEvent(Event event) {
        Event delayedEvent = applyDelay(event);
        eventQueue.add(delayedEvent);
    }

    /**
     * Clears the {@link Simulator#eventQueue}
     */
    public void clearEvents() {
        eventQueue.clear();
    }

    /**
     * @param event The event to be delayed
     * @return A new event that has the same information as the event passed, save a delay added to it
     */
    private Event applyDelay(Event event) {
        Duration delay = Duration.ofMillis(random.nextInt(1, MAX_LATENCY));
        Duration eventTime = event.time().plus(delay);
        return new Event(eventTime, event.runnable());
    }

    /**
     * Removes each {@link Event} in the {@link Simulator#eventQueue}, calling it, until there remain no more events
     */
    @Override
    public void run() {
        while (!eventQueue.isEmpty()) {
            Event event = eventQueue.remove();
            currentTime = event.time();
            event.run();
        }
        System.out.println("[DEBUG] Final time: " + currentTime.toMillis() + "ms");
    }
}
