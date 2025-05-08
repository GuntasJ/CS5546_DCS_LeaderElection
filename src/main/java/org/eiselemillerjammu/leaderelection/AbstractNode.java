package org.eiselemillerjammu.leaderelection;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class that contains boilerplate code for all leader election {@code nodes}.
 * @param <M> The leader election message class
 */
public abstract class AbstractNode<M extends Message> implements Node<M> {
    /**
     * The unique id of the node
     */
    protected final int id;

    /**
     * The simulator reference of the node
     */
    protected final Simulator simulator;

    /**
     * The group of which this {@link AbstractNode} is a part of, which includes this {@link AbstractNode}
     */
    protected final List<Node<M>> groupNodes;

    /**
     * True if this node is alive, false otherwise
     */
    protected boolean isAlive;

    /**
     * The leader of this {@code Node} group
     */
    protected Node<M> coordinator;

    /**
     * Initialize all basic fields
     * @param id The unique id of the {@code node}
     * @param simulator The simulator reference of the {@code node}
     */
    public AbstractNode(int id, Simulator simulator) {
        this.id = id;
        this.simulator = simulator;
        groupNodes = new ArrayList<>();
        coordinator = null;
        isAlive = true;
    }

    @Override
    public void fail() {
        isAlive = false;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public void recover() {
        isAlive = true;
    }

    @Override
    public Simulator simulator() {
        return simulator;
    }

    @Override
    public Node<M> leader() {
        return coordinator;
    }

    @Override
    public List<Node<M>> groupNodes() {
        return groupNodes;
    }

    @Override
    public String toString() {
        return "Node[" + "id=" + id + ']';
    }
}
