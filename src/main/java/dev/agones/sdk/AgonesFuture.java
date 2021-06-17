package dev.agones.sdk;

import io.grpc.stub.StreamObserver;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

class AgonesFuture<T, R> extends CompletableFuture<T> implements StreamObserver<R> {

    private final Function<R, T> valueMapper;

    public AgonesFuture() {
        this(original -> null);
    }

    public AgonesFuture(Function<R, T> valueMapper) {
        super();

        this.valueMapper = valueMapper;
    }

    @Override
    public void onNext(R value) {
        complete(valueMapper.apply(value));
    }

    @Override
    public void onError(Throwable t) {
        completeExceptionally(t);
    }

    @Override
    public void onCompleted() {
        //Not needed, this observer implementation is not used for the streams
    }
}
