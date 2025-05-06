package org.jammu.leaderelection.bully;

import org.jammu.leaderelection.Event;
import org.jammu.leaderelection.Simulator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BullyNode {
    private static final Duration TIMEOUT = Duration.ofMillis(1_000);
    private final int id;
    private final Simulator simulator;
    private BullyNode coordinator;
    private final List<BullyNode> groupNodes;
    private boolean isAlive;
    private boolean waiting;


    public BullyNode(int id, Simulator simulator) {
        this.id = id;
        this.simulator = simulator;
        coordinator = null;
        groupNodes = new ArrayList<>();
        isAlive = true;
        waiting = false;
    }

    public int getId() {
        return id;
    }

    public void fail() {
        isAlive = false;
        if (this == coordinator) {
            groupNodes.forEach(node -> node.coordinator = null);
        }
    }
    public void recover() {
        isAlive = true;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void startElection() {
        if (isAlive) {
            BullyMessage message = new BullyMessage.Election(this);
            groupNodes.stream()
                    .filter(node -> node.id > id)
                    .forEach(destinationNode -> sendMessage(destinationNode, message));

            waitForAnswer();
        }
    }

    private void waitForAnswer() {
        waiting = true;
        simulator.addEvent(new Event(simulator.getCurrentTime().plus(TIMEOUT), () -> {
            if (waiting) {
                simulator.clearEvents();
                System.out.println("[DEBUG]: Coordinator is " + this);
                System.out.println("[DEBUG]: Time is " + simulator.getCurrentTime().toMillis() + "ms");
                groupNodes.stream()
                        .filter(BullyNode::isAlive)
                        .forEach(node -> sendMessage(node, new BullyMessage.Coordinator(this)));
            }
        }));
    }

    private void sendMessage(BullyNode destination, BullyMessage message) {
        System.out.println("[DEBUG]: " + this + " is sending message " + message + " to " + destination);
        Event event = new Event(simulator.getCurrentTime(), () -> destination.receiveMessage(message));
        simulator.addEvent(event);
    }

    private void receiveMessage(BullyMessage message) {
        if (isAlive) {
            switch (message) {
                case BullyMessage.Election(var source) -> {
                    sendMessage(source, new BullyMessage.Answer());
                    startElection();
                }
                case BullyMessage.Answer() -> waiting = false;
                case BullyMessage.Coordinator(var source) -> coordinator = source;
            }
        }
    }

    public List<BullyNode> getGroupNodes() {
        return groupNodes;
    }

    @Override
    public String toString() {
        return "Node[" + "id=" + id + ']';
    }
}
