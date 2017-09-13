package it.dfalciglia.sample.reactive.rxjava;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RxJavaSubscriber implements Subscriber<String> {

    private static final Logger log = LoggerFactory.getLogger(RxJavaSubscriber.class);

    static final Set<String> ELABORATED_UUIDS = ConcurrentHashMap.newKeySet();
    private static final AtomicLong processedItems = new AtomicLong();

    private Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        log.info("Subscription {} started.", subscription);
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(String item) {
        log.info("Processed item {}, total processed items {}", item, processedItems.incrementAndGet());
        ELABORATED_UUIDS.add(item);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("Error consuming data: {}", throwable.getMessage(), throwable);
    }

    @Override
    public void onComplete() {
        log.info("Subscription completed.");
    }

}
