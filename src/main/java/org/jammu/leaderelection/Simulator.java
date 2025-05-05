package org.jammu.leaderelection;

import java.time.Duration;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SplittableRandom;

public class Simulator implements Runnable {
    private static final int MAX_LATENCY = 100;
    private final Queue<Event> eventQueue;
    private final SplittableRandom random;
    private Duration currentTime;

    public Simulator() {
        eventQueue = new PriorityQueue<>(Comparator.comparing(Event::time));
        random = new SplittableRandom(0);
        currentTime = Duration.ZERO;
    }

    public Duration getCurrentTime() {
        return currentTime;
    }

    public void addEvent(Event event) {
        Event delayedEvent = applyDelay(event);
        eventQueue.add(delayedEvent);
    }

    public void clearEvents() {
        eventQueue.clear();
    }

    private Event applyDelay(Event event) {
        Duration delay = Duration.ofMillis(random.nextInt(1, MAX_LATENCY));
        Duration eventTime = event.time().plus(delay);
        return new Event(eventTime, event.runnable());
    }

    @Override
    public void run() {
        while (!eventQueue.isEmpty()) {
            Event event = eventQueue.remove();
            currentTime = event.time();
            event.run();
        }
    }
}
