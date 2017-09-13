package it.dfalciglia.sample.reactive.javaflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicLong;

public class JavaFlowSubscriber implements Flow.Subscriber<String> {

    static final Set<String> ELABORATED_UUIDS = ConcurrentHashMap.newKeySet();

    private static final Logger log = LoggerFactory.getLogger(JavaFlowSubscriber.class);

    private static final AtomicLong processedItems = new AtomicLong();

    private Flow.Subscription subscription;

    public void onSubscribe(Flow.Subscription subscription) {
        log.info("Subscription {} started.", subscription);
        this.subscription = subscription;
        subscription.request(1);
    }

    public void onNext(String item) {
        log.debug("Processed item {}, total processed items {}", item, processedItems.incrementAndGet());
        ELABORATED_UUIDS.add(item);
        subscription.request(1);
    }

    public void onError(Throwable throwable) {
        log.error("Error consuming data: {}", throwable.getMessage(), throwable);
    }

    public void onComplete() {
        log.info("Subscription completed.");
    }
}
