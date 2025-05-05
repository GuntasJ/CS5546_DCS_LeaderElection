package org.jammu.eventprocessing;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SplittableRandom;

public class EventManager {
    private final Queue<Event<?>> eventQueue;
    private final SplittableRandom random;

    public EventManager() {
        eventQueue = new PriorityQueue<>(Comparator.comparing(Event::time));
    }

    public void addEvent() {

    }

}
