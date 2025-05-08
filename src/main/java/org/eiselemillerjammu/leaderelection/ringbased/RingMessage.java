package org.eiselemillerjammu.leaderelection.ringbased;

import org.eiselemillerjammu.leaderelection.Message;

/**
 * An interface that represents a ring-based message
 */
public sealed interface RingMessage extends Message {

    /**
     * An election message
     * @param nominatedId The node id that has been nominated
     */
    record Election(int nominatedId) implements RingMessage{}

    /**
     * An elected message
     * @param electedId The node id that was elected by the ring
     */
    record Elected(int electedId) implements RingMessage{}
}
