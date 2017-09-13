package it.dfalciglia.sample.reactive.reactor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.bus.Event;
import reactor.fn.Consumer;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class ReactorSubscriber implements Consumer<Event<String>> {

    private static final Logger log = LoggerFactory.getLogger(ReactorSubscriber.class);
    static final Set<String> ELABORATED_UUIDS = ConcurrentHashMap.newKeySet();
    private static final AtomicLong processedItems = new AtomicLong();


    private CountDownLatch countDownLatch;

    @Override
    public void accept(Event<String> stringEvent) {
        String item = stringEvent.getData();
        log.debug("Processed item {}, total processed items {}", item, processedItems.incrementAndGet());
        ELABORATED_UUIDS.add(item);
        countDownLatch.countDown();
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }
}
