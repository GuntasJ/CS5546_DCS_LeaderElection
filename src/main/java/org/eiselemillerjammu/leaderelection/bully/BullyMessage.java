package org.eiselemillerjammu.leaderelection.bully;

import org.eiselemillerjammu.leaderelection.Message;

/**
 * An interface that represents a bully message
 */
public sealed interface BullyMessage extends Message {
    /**
     * An election message
     * @param source The node from which this message came from, which is also the proposed leader
     */
    record Election(BullyNode source) implements BullyMessage{}

    /**
     * The answer message that is sent by higher valued nodes to lower ones
     */
    record Answer() implements BullyMessage{}

    /**
     * The final message indicating to all living nodes who the leader is
     * @param source The node from which this message came from, which is also the elected leader
     */
    record Coordinator(BullyNode source) implements BullyMessage{}
}
