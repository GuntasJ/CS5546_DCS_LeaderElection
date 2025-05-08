package org.eiselemillerjammu.leaderelection.paxos;

import org.eiselemillerjammu.leaderelection.Message;

/**
 * An interface that represents a paxos message
 */
public sealed interface PaxosMessage extends Message {
    /**
     * A prepare message sent to acceptors by the proposer
     * @param roundIdentifier The round identifier
     * @param source The proposer
     */
    record Prepare(RoundIdentifier roundIdentifier, PaxosNode source) implements PaxosMessage{}

    /**
     * A promise message sent to the proposer by the acceptors
     * @param roundIdentifier
     */
    record Promise(RoundIdentifier roundIdentifier) implements PaxosMessage{}

    /**
     * An accept message sent to the acceptors by the proposer
     * @param roundIdentifier
     * @param proposedNode The node which is proposed to be the leader
     */
    record Accept(RoundIdentifier roundIdentifier, PaxosNode proposedNode) implements PaxosMessage{}

    /**
     * An accepted message sent to all nodes indicating the leader
     * @param acceptedNode The accepted leader
     */
    record Accepted(PaxosNode acceptedNode) implements PaxosMessage{}
}
