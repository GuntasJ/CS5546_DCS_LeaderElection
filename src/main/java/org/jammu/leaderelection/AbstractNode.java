package org.jammu.leaderelection;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNode<M extends Message> implements Node<M> {
    protected final int id;
    protected final Simulator simulator;
    protected final List<Node<M>> groupNodes;

    protected boolean isAlive;
    protected Node<M> coordinator;

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
