package org.eiselemillerjammu.leaderelection.raft;

import org.eiselemillerjammu.leaderelection.Message;

/**
 * An interface that represents a raft message
 */
public sealed interface RaftMessage extends Message {

    /**
     * A request vote message, sent by the proposer
     * @param electionTerm The term this election is in
     * @param source The proposer
     */
    record RequestVote(int electionTerm, RaftNode source) implements RaftMessage{}

    /**
     * A vote message, sent by acceptors
     */
    record Vote() implements RaftMessage{}

    /**
     * A notify result message, sent by the proposer
     * @param leader The new leader, which in leader elections, is the proposer
     */
    record NotifyResult(RaftNode leader) implements RaftMessage{}

    /**
     * A heartbeat message that is sent to acceptors by the proposer
     */
    record Heartbeat() implements RaftMessage{}
}