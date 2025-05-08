package org.jammu.leaderelection.paxos;

import org.jammu.leaderelection.Message;

public sealed interface PaxosMessage extends Message {
    record Prepare(RoundIdentifier roundIdentifier, PaxosNode source) implements PaxosMessage{}
    record Promise(RoundIdentifier roundIdentifier) implements PaxosMessage{}
    record Accept(RoundIdentifier roundIdentifier, PaxosNode proposedNode) implements PaxosMessage{}
    record Accepted(PaxosNode acceptedNode) implements PaxosMessage{}
}
