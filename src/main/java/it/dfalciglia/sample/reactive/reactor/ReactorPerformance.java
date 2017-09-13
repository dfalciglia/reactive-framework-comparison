package it.dfalciglia.sample.reactive.reactor;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Control;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import reactor.Environment;
import reactor.bus.Event;
import reactor.bus.EventBus;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static reactor.bus.selector.Selectors.$;


@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
public class ReactorPerformance {


    Publisher publisher;
    CountDownLatch latch;
    Environment env = Environment.initializeIfEmpty()
            .assignErrorJournal();
    EventBus eventBus = EventBus.create(env, Environment.THREAD_POOL);
    static ReactorSubscriber subscriber = new ReactorSubscriber();
    private String uuid;

    private Callable<? extends String> callable = () -> UUID.randomUUID().toString();

    @Setup(Level.Iteration)
    public void subscribe() throws Exception {
        publisher = new Publisher();
        latch = new CountDownLatch(1);
        subscriber.setCountDownLatch(latch);
        eventBus.on($("uuid"), subscriber);
    }

    @TearDown(Level.Iteration)
    public void close() {

    }

    @Setup(Level.Invocation)
    public void publish() throws Exception {
        uuid = UUID.randomUUID().toString();
        publisher.submit(uuid);
    }

    @Benchmark
    @Warmup(iterations = 5, time = 1)
    @Measurement(iterations = 5, time = 60)
    public void measure(Control control) {
        while (!control.stopMeasurement && !ReactorSubscriber.ELABORATED_UUIDS.remove(uuid)) {
            // this body is intentionally left blank
        }
    }


    public class Publisher {

        public void submit(String uuid) throws InterruptedException {
            eventBus.notify("uuid", Event.wrap(uuid));
            latch.await();
        }

    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ReactorPerformance.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}
