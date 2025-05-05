package org.jammu.leaderelection.ringbased;

import org.jammu.leaderelection.Event;
import org.jammu.leaderelection.Simulator;

public class RingNode {
    private final int id;
    private final Simulator simulator;
    private RingNode nextNode;
    private boolean inElection;

    public RingNode(int id, Simulator simulator) {
        this.id = id;
        this.simulator = simulator;
        inElection = false;
    }

    public int getId() {
        return id;
    }

    public void setNextNode(RingNode nextNode) {
        this.nextNode = nextNode;
    }

    public void sendMessage(RingMessage message) {
        System.out.println("[DEBUG]: " + " Sending message " + message + " to Node[" + nextNode.id + "]");
        Event event = new Event(simulator.getCurrentTime(), () -> nextNode.receiveMessage(message));
        simulator.addEvent(event);
    }

    public void startElection() {
        inElection = true;
        sendMessage(new RingMessage.Election(id));
    }

    public void receiveMessage(RingMessage message) {
        switch (message) {
            case RingMessage.Election(int nominatedId) -> {
                inElection = true;
                if (id > nominatedId) {
                    sendMessage(new RingMessage.Election(id));
                }
                else if (id < nominatedId) {
                    sendMessage(message);
                }
                else {
                    sendMessage(new RingMessage.Elected(id));
                }
            }
            case RingMessage.Elected(int electionId) -> {
                inElection = false;
                if (id != electionId) {
                    sendMessage(message);
                }
                else {
                    System.out.println("Leader elected with id of " + id);
                    System.out.println("Time: " + simulator.getCurrentTime().toMillis() + "ms");
                }
            }
        }
    }

    @Override
    public String toString() {
        return "RingNode[" +
                "id=" + id +
                ']';
    }
}
