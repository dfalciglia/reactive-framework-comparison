package it.dfalciglia.sample.reactive.javaflow;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Control;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.UUID;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;


@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
public class JavaFlowPerformance {

    static SubmissionPublisher<String> publisher;
    static JavaFlowSubscriber subscriber = new JavaFlowSubscriber();
    String uuid;

    @Setup(Level.Iteration)
    public void subscribe() {
        publisher = new SubmissionPublisher<>();
        publisher.subscribe(subscriber);
    }

    @TearDown(Level.Iteration)
    public void close() {
        publisher.close();
    }


    @Setup(Level.Invocation)
    public void publish() {
        uuid = UUID.randomUUID().toString();
        publisher.submit(uuid);
    }

    @Benchmark
    @Warmup(iterations = 5, time = 1)
    @Measurement(iterations = 5, time = 60)
    public void measure(Control control) {
        while (!control.stopMeasurement && !JavaFlowSubscriber.ELABORATED_UUIDS.remove(uuid)) {
            // this body is intentionally left blank
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JavaFlowPerformance.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}
