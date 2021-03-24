package de.hhu.bsinfo.infinileap.example.util;

import de.hhu.bsinfo.infinileap.binding.Worker;

public class RequestPool {
    
    private final long[] requests;

    private int consumerIndex;
    private int producerIndex;


    public RequestPool(int size) {
        requests = new long[size];
    }

    public final void add(long request) {
        requests[producerIndex++] = request;
    }

    public final void pollRemaining(Worker worker) {
        for (int i = consumerIndex; i < producerIndex; i++) {
            Requests.poll(worker, requests[i]);
        }

        producerIndex = 0;
        consumerIndex = 0;
    }

    public final void poll(Worker worker) {
        Requests.poll(worker, requests[consumerIndex++]);
    }

    public final void rewind() {
        producerIndex = 0;
        consumerIndex = 0;
    }
}
