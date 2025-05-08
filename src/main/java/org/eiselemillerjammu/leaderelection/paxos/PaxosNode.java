package org.eiselemillerjammu.leaderelection.paxos;

import org.eiselemillerjammu.leaderelection.AbstractNode;
import org.eiselemillerjammu.leaderelection.Event;
import org.eiselemillerjammu.leaderelection.Simulator;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that represents a paxos node in the paxos algorithm
 */
public final class PaxosNode extends AbstractNode<PaxosMessage> {

    /**
     * The greatest round identifier seen so far
     */
    private RoundIdentifier greatestRoundIdentifier;

    /**
     * An accepted log which keeps track of accepted leaders.
     * This log is not ever queried; however, it is used in
     * the consensus portion of this algorithm.
     * This implementation only is concerned with the leader election
     * portion, and the log is only modified, not queried, in this phase
     */
    private final Map<RoundIdentifier, PaxosNode> acceptedLog;

    /**
     * How many {@link org.eiselemillerjammu.leaderelection.paxos.PaxosMessage.Accepted} messages this node has received
     */
    private int acceptedReceived;

    /**
     * True if this node can send another {@link org.eiselemillerjammu.leaderelection.paxos.PaxosMessage.Accept} message,
     * false otherwise
     */
    private boolean canSendAccept;

    /**
     * How many {@link org.eiselemillerjammu.leaderelection.paxos.PaxosMessage.Promise} messages this node has received
     */
    private int promisesReceived;

    /**
     * True if this node is waiting for a response, false otherwise
     */
    private boolean waiting;

    /**
     * Initialize basic field values
     * @param id The unique id of the node
     * @param simulator The simulator reference of the node
     */
    public PaxosNode(int id, Simulator simulator) {
        super(id, simulator);
        isAlive = true;
        greatestRoundIdentifier = new RoundIdentifier(-1, -1);
        acceptedLog = new HashMap<>();

        acceptedReceived = 0;
        promisesReceived = 0;
        canSendAccept = true;
        waiting = false;
    }

    @Override
    public void startElection() {
        greatestRoundIdentifier = new RoundIdentifier(1, id);
        broadcastOthers(new PaxosMessage.Prepare(greatestRoundIdentifier, this));
    }

    @Override
    public void receiveMessage(PaxosMessage message) {
        if (isAlive) {
            switch (message) {
                case PaxosMessage.Prepare(var roundIdentifier, var source) -> {
                    if (roundIdentifier.compareTo(greatestRoundIdentifier) > 0) {
                        greatestRoundIdentifier = roundIdentifier;
                        sendMessage(source, new PaxosMessage.Promise(roundIdentifier));

                        waiting = true;
                        simulator.addEvent(new Event(simulator.getCurrentTime().plus(Duration.ofMillis(1_000)), () -> {
                            if (waiting) {
                                greatestRoundIdentifier = new RoundIdentifier(greatestRoundIdentifier.greatestSoFar() + 1, id);
                                startElection();
                            }
                        }));
                    }
                }

                case PaxosMessage.Promise(var roundIdentifier) -> {
                    promisesReceived++;
                    if (canSendAccept && promisesReceived > groupNodes.size() / 2) {
                        broadcastOthers(new PaxosMessage.Accept(roundIdentifier, this));
                        canSendAccept = false;
                    }
                }

                case PaxosMessage.Accept(var roundIdentifier, var proposedNode) -> {
                    waiting = false;
                    if (greatestRoundIdentifier.equals(roundIdentifier)) {
                        acceptedLog.put(roundIdentifier, proposedNode);
                        coordinator = proposedNode;
                        broadcastOthers(new PaxosMessage.Accepted(proposedNode));
                    }
                }

                case PaxosMessage.Accepted(var acceptedNode) -> {
                    acceptedReceived++;
                    if (acceptedReceived > groupNodes.size() / 2) {
                        System.out.println("[DEBUG]: " + this + " has elected " + acceptedNode);
                        coordinator = acceptedNode;
                        acceptedReceived = Integer.MIN_VALUE;
                    }
                }
            }
        }
    }
}
