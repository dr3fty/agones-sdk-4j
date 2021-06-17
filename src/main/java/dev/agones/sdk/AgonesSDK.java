package dev.agones.sdk;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * An instance of the Agones SDK
 */
public class AgonesSDK {

    private final SDKGrpc.SDKStub client;
    private StreamObserver<Sdk.Empty> healthStream;

    private final AgonesAlphaSDK alpha;

    private AgonesSDK(int port) {
        this(ManagedChannelBuilder.forAddress("localhost", port)
                .usePlaintext()
                .build());
    }

    private AgonesSDK(ManagedChannel channel) {
        this.client = SDKGrpc.newStub(channel);

        this.alpha = new AgonesAlphaSDK(channel);
    }

    /**
     * Create new instance of AgonesSDK.
     * <p>
     * Uses {@literal AGONES_SDK_GRPC_PORT} environment variable to get the port,
     * or default (9357) if not present.
     *
     * @return AgonesSDK instance
     */
    public static AgonesSDK create() {
        int port = System.getenv("AGONES_SDK_GRPC_PORT") == null ?
                9357 : Integer.parseInt(System.getenv("AGONES_SDK_GRPC_PORT"));

        return new AgonesSDK(port);
    }

    /**
     * Create new instance of AgonesSDK.
     *
     * @param port gRPC service port
     * @return AgonesSDK instance
     */
    public static AgonesSDK create(int port) {
        return new AgonesSDK(port);
    }

    /**
     * Returns the Alpha SDK
     *
     * @return Alpha SDK
     */
    public AgonesAlphaSDK alpha() {
        return this.alpha;
    }

    /**
     * Marks the Game Server as ready to receive connections
     *
     * @return void
     */
    public CompletableFuture<Void> ready() {
        AgonesFuture<Void, Sdk.Empty> future = new AgonesFuture<>();

        this.client.ready(Sdk.Empty.getDefaultInstance(), future);

        return future;
    }

    /**
     * Marks the Game Server as allocated
     *
     * @return void
     */
    public CompletableFuture<Void> allocate() {
        AgonesFuture<Void, Sdk.Empty> future = new AgonesFuture<>();

        this.client.allocate(Sdk.Empty.getDefaultInstance(), future);

        return future;
    }

    /**
     * Marks the Game Server as ready to shutdown
     *
     * @return void
     */
    public CompletableFuture<Void> shutdown() {
        AgonesFuture<Void, Sdk.Empty> future = new AgonesFuture<>();

        this.client.shutdown(Sdk.Empty.getDefaultInstance(), future);

        return future;
    }

    /**
     * Sends a ping to the health check to indicate that this Game Server is healthy
     */
    public void health() {
        if (this.healthStream == null) {
            this.healthStream = this.client.health(new StreamObserver<Sdk.Empty>() {
                @Override
                public void onNext(Sdk.Empty value) {

                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {

                }
            });
        }

        this.healthStream.onNext(Sdk.Empty.getDefaultInstance());
    }

    /**
     * Returns most of the backing {@link AgonesGameServer} configuration and status
     *
     * @return a {@link AgonesGameServer} with this Game Server's configuration data
     */
    public CompletableFuture<AgonesGameServer> getGameServer() {
        AgonesFuture<AgonesGameServer, Sdk.GameServer> future =
                new AgonesFuture<>(AgonesMappers.GAME_SERVER_MAPPER);

        this.client.getGameServer(Sdk.Empty.getDefaultInstance(), future);

        return future;
    }

    /**
     * Executes the {@code callback} with the current {@link AgonesGameServer} details
     * whenever the underlying {@link AgonesGameServer} configuration is updated, or an
     * exception if an error occurred
     *
     * @param callback {@link AgonesGameServer} and {@link Throwable} consumer
     */
    public void watchGameServer(BiConsumer<AgonesGameServer, Throwable> callback) {
        this.client.watchGameServer(Sdk.Empty.getDefaultInstance(), new StreamObserver<Sdk.GameServer>() {
            @Override
            public void onNext(Sdk.GameServer value) {
                callback.accept(AgonesGameServer.fromProto(value), null);
            }

            @Override
            public void onError(Throwable t) {
                callback.accept(null, t);
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    /**
     * Sets a Label value on the backing {@link AgonesGameServer} record that is stored in Kubernetes
     *
     * @param key Label key
     * @param value Label value
     * @return void
     *
     * @see <a href="https://kubernetes.io/docs/concepts/overview/working-with-objects/labels/">Kubernetes Labels</a>
     */
    public CompletableFuture<Void> setLabel(String key, String value) {
        AgonesFuture<Void, Sdk.Empty> future = new AgonesFuture<>();

        Sdk.KeyValue keyValue = Sdk.KeyValue.newBuilder()
                .setKey(key)
                .setValue(value)
                .build();

        this.client.setLabel(keyValue, future);

        return future;
    }

    /**
     * Sets an Annotation value on the backing {@link AgonesGameServer} record that is stored in Kubernetes
     *
     * @param key Annotation key
     * @param value Annotation value
     * @return void
     *
     * @see <a href="https://kubernetes.io/docs/concepts/overview/working-with-objects/annotations/">Kubernetes Annotations</a>
     */
    public CompletableFuture<Void> setAnnotation(String key, String value) {
        AgonesFuture<Void, Sdk.Empty> future = new AgonesFuture<>();

        Sdk.KeyValue keyValue = Sdk.KeyValue.newBuilder()
                .setKey(key)
                .setValue(value)
                .build();

        this.client.setAnnotation(keyValue, future);

        return future;
    }

    /**
     * Marks the Game Server as reserved for a given duration (in seconds),
     * at which point it will return the Game Server to a ready state
     *
     * @param seconds Duration for the Game Server to be marked as reserved
     * @return void
     */
    public CompletableFuture<Void> reserve(int seconds) {
        AgonesFuture<Void, Sdk.Empty> future = new AgonesFuture<>();

        Sdk.Duration duration = Sdk.Duration.newBuilder()
                .setSeconds(seconds)
                .build();

        this.client.reserve(duration, future);

        return future;
    }
}
