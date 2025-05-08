package org.jammu.leaderelection.paxos;

import org.jammu.leaderelection.AbstractNode;
import org.jammu.leaderelection.Event;
import org.jammu.leaderelection.Simulator;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public final class PaxosNode extends AbstractNode<PaxosMessage> {
    private RoundIdentifier greatestRoundIdentifier;
    private final Map<RoundIdentifier, PaxosNode> acceptedLog;
    private int acceptedReceived;
    private boolean canSendAccept;
    private int promisesReceived;
    private boolean waiting;

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
                    }
                }
            }
        }
    }
}
