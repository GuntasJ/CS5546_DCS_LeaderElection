package org.jammu.leaderelection.ringbased;

import org.jammu.leaderelection.AbstractNode;
import org.jammu.leaderelection.Simulator;

public final class RingNode extends AbstractNode<RingMessage> {
    private RingNode nextNode;
    private boolean inElection;

    public RingNode(int id, Simulator simulator) {
        super(id, simulator);
        inElection = false;
    }

    public void setNextNode(RingNode nextNode) {
        this.nextNode = nextNode;
    }

    public void startElection() {
        inElection = true;
        sendMessage(nextNode, new RingMessage.Election(id));
    }

    @Override
    public void receiveMessage(RingMessage message) {
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
                    System.out.println("Leader elected with id of " + id);
                    System.out.println("Time: " + simulator.getCurrentTime().toMillis() + "ms");
                }
            }
        }
    }
}
