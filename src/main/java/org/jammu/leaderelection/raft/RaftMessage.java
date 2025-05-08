package org.jammu.leaderelection.raft;

import org.jammu.leaderelection.Message;

public sealed interface RaftMessage extends Message {
    record RequestVote(int electionTerm, RaftNode source) implements RaftMessage{}
    record Vote() implements RaftMessage{}
    record NotifyResult(RaftNode leader) implements RaftMessage{}
    record Heartbeat() implements RaftMessage{}
}