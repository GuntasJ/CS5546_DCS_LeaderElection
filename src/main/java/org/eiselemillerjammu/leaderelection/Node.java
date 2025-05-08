package org.eiselemillerjammu.leaderelection;

import java.util.List;

/**
 * A top level interface that represents a leader election node
 * @param <M> The top level message interface that represents a leader election message
 */
public interface Node<M extends Message> {

    /**
     * @return The unique id of a node
     */
    int id();

    /**
     * @return True if the node is alive, false otherwise
     */
    boolean isAlive();

    /**
     * Modifies the node such that {@link Node#isAlive()} returns false
     */
    void fail();

    /**
     * Modifies the node such that {@link Node#isAlive()} returns true
     */
    void recover();

    /**
     * @return The simulator reference for each {@link Node<M>}
     */
    Simulator simulator();

    /**
     * @return The leader {@link Node<M>} of the group. Leader and coordinator are used interchangeably in {@link Node}
     */
    Node<M> leader();

    /**
     * @return The group of which this {@link Node<M>} is a part of, which includes this {@link Node<M>}
     */
    List<Node<M>> groupNodes();

    /**
     * Has this {@link Node<M>} start the election
     */
    void startElection();

    /**
     * Receives a message from another {@link Node<M>}, and depending on that message, and leader election algorithm,
     * executes further steps for that algorithm.
     * Often times, another message will be sent on each {@code receiveMessage} call
     * @param message The message that is received
     */
    void receiveMessage(M message);

    /**
     * Sends a message to the destination {@link Node<M>} by adding an event for the {@link Node#receiveMessage(Message)}
     * method to be called to the Simulator's {@code eventQueue}
     * @param destination The {@link Node<M>} to where the message should be sent
     * @param message The message to be sent
     */
    default void sendMessage(Node<M> destination, M message) {
        System.out.println("[DEBUG]: " + this + " is sending message " + message + " to " + destination);
        Event event = new Event(simulator().getCurrentTime(), () -> destination.receiveMessage(message));
        simulator().addEvent(event);
    }

    /**
     * Sends a message to all other nodes in the group, excluding itself
     * @param message The message to be sent
     */
    default void broadcastOthers(M message) {
        for (var node : groupNodes()) {
            if (node.id() != id()) {
                sendMessage(node, message);
            }
        }
    }
}
