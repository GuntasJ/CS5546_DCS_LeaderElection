package org.jammu.leaderelection.bully;

public sealed interface BullyMessage {
    record Election(BullyNode source) implements BullyMessage{}
    record Answer() implements BullyMessage{}
    record Coordinator(BullyNode source) implements BullyMessage{}
}
