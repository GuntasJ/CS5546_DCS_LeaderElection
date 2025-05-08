package org.jammu.leaderelection.paxos;

public record RoundIdentifier(int greatestSoFar, int nodeId) implements Comparable<RoundIdentifier> {

    @Override
    public int compareTo(RoundIdentifier o) {
        if (greatestSoFar != o.greatestSoFar) {
            return Integer.compare(greatestSoFar, o.greatestSoFar);
        }
        return Integer.compare(nodeId, o.nodeId);
    }
}
