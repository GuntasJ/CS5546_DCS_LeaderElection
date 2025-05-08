package org.jammu.leaderelection;

import java.util.List;

public interface Node<M extends Message> {
    int id();

    boolean isAlive();

    void fail();

    void recover();

    Simulator simulator();

    Node<M> leader();

    List<Node<M>> groupNodes();

    void startElection();

    void receiveMessage(M message);

    default void sendMessage(Node<M> destination, M message) {
        System.out.println("[DEBUG]: " + this + " is sending message " + message + " to " + destination);
        Event event = new Event(simulator().getCurrentTime(), () -> destination.receiveMessage(message));
        simulator().addEvent(event);
    }

    default void broadcastOthers(M message) {
        for (var node : groupNodes()) {
            if (node.id() != id()) {
                sendMessage(node, message);
            }
        }
    }
}
