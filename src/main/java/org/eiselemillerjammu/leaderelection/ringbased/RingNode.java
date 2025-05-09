package org.eiselemillerjammu.leaderelection.ringbased;

import org.eiselemillerjammu.leaderelection.AbstractNode;
import org.eiselemillerjammu.leaderelection.Simulator;

/**
 * A class that represents a Ring node used in the Ring-based leader election
 */
public final class RingNode extends AbstractNode<RingMessage> {

    /**
     * The next node to this node in the ring
     */
    private RingNode nextNode;

    /**
     * True if this node is participating in a Ring-based election, false otherwise
     */
    private boolean inElection;

    /**
     * Initializes basic values
     * @param id The unique id of the {@code Node}
     * @param simulator The simulator reference for this {@code Node}
     */
    public RingNode(int id, Simulator simulator) {
        super(id, simulator);
        inElection = false;
    }

    /**
     * Set the {@link RingNode} to be the next for this {@link RingNode}
     * @param nextNode The {@code node} to be set at this {@code node's} next node
     */
    public void setNextNode(RingNode nextNode) {
        this.nextNode = nextNode;
    }

    @Override
    public void startElection() {
        if (!isAlive) {
            throw new IllegalStateException("Node is not alive");
        }
        inElection = true;
        sendMessage(nextNode, new RingMessage.Election(id));
    }

    @Override
    public void receiveMessage(RingMessage message) {
        if (!isAlive) {
            return;
        }

        switch (message) {
            case RingMessage.Election(int nominatedId) -> {
                inElection = true;
                if (id > nominatedId) {
                    sendMessage(nextNode, new RingMessage.Election(id));
                }
                else if (id < nominatedId) {
                    sendMessage(nextNode, message);
                }
                else {
                    sendMessage(nextNode, new RingMessage.Elected(id));
                }
            }
            case RingMessage.Elected(int electionId) -> {
                inElection = false;
                if (id != electionId) {
                    sendMessage(nextNode, message);
                }
                else {
//                    System.out.println("Leader elected with id of " + id);
                }
            }
        }
    }
}
