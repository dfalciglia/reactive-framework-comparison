package it.dfalciglia.sample.reactive.rxjava;

import io.reactivex.Flowable;
import it.dfalciglia.sample.reactive.javaflow.JavaFlowSubscriber;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Control;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
public class RxJavaPerformance {

    static Flowable<String> publisher;
    static RxJavaSubscriber subscriber = new RxJavaSubscriber();
    private String uuid;

    private Callable<? extends String> callable = () -> UUID.randomUUID().toString();

    @Setup(Level.Iteration)
    public void subscribe() throws Exception {
        publisher = Flowable.fromCallable(callable);
        publisher.subscribe(subscriber);
    }

    @TearDown(Level.Iteration)
    public void close() {

    }

    @Setup(Level.Invocation)
    public void publish() throws Exception {
        uuid = callable.call();
    }

    @Benchmark
    @Warmup(iterations = 5, time = 1)
    @Measurement(iterations = 5, time = 60)
    public void measure(Control control) {
        while (!control.stopMeasurement && !RxJavaSubscriber.ELABORATED_UUIDS.remove(uuid)) {
            // this body is intentionally left blank
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(RxJavaPerformance.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}
