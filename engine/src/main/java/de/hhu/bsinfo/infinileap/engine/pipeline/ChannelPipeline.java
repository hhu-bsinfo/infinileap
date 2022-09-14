package de.hhu.bsinfo.infinileap.engine.pipeline;

import de.hhu.bsinfo.infinileap.engine.channel.Channel;

import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.List;

public class ChannelPipeline {

    private final List<MessageProcessor> processors;

    public ChannelPipeline() {
        this.processors = new ArrayList<>();
    }

    public void add(MessageProcessor processor) {
        processors.add(processor);
    }

    public void run(Channel source, int identifier, MemorySegment header, MemorySegment body) {
        for (var processor : processors) {
            processor.process(source, identifier, header, body);
        }
    }
}
