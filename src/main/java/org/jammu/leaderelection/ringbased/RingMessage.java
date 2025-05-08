package org.jammu.leaderelection.ringbased;

import org.jammu.leaderelection.Message;

public sealed interface RingMessage extends Message {

    record Election(int nominatedId) implements RingMessage{}

    record Elected(int electedId) implements RingMessage{}
}
