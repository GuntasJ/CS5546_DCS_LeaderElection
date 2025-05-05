package org.jammu.leaderelection.ringbased;

public sealed interface RingMessage {

    record Election(int nominatedId) implements RingMessage{}

    record Elected(int electedId) implements RingMessage{}
}
