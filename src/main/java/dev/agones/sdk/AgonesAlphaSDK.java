package dev.agones.sdk;

import dev.agones.sdk.alpha.Alpha;
import dev.agones.sdk.alpha.SDKGrpc;
import io.grpc.ManagedChannel;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * An instance of the Alpha Agones SDK
 */
public class AgonesAlphaSDK {

    private final SDKGrpc.SDKStub client;

    AgonesAlphaSDK(ManagedChannel channel) {
        this.client = SDKGrpc.newStub(channel);
    }

    /**
     * Increases the SDK's stored player count by one, and appends this {@code playerId}
     * to {@link AgonesGameServer.Status.PlayerStatus#getPlayers()}
     *
     * @param playerId Player id to add
     * @return true and adds the {@code playerId} to the list of playerIDs
     * if this {@code playerId} was not already in the list of connected playerIDs.
     */
    public CompletableFuture<Boolean> playerConnect(String playerId) {
        AgonesFuture<Boolean, Alpha.Bool> future =
                new AgonesFuture<>(AgonesMappers.ALPHA_BOOL_MAPPER);

        Alpha.PlayerID playerID = Alpha.PlayerID
                .newBuilder()
                .setPlayerID(playerId)
                .build();

        this.client.playerConnect(playerID, future);

        return future;
    }

    /**
     * Decreases the SDK’s stored player count by one, and removes the {@code playerId} from
     * {@link AgonesGameServer.Status.PlayerStatus#getPlayers()}
     *
     * @param playerId Player id to remove
     * @return true and remove the supplied {@code playerId} from the list of connected playerIDs
     * if the {@code playerId} value exists within the list.
     */
    public CompletableFuture<Boolean> playerDisconnect(String playerId) {
        AgonesFuture<Boolean, Alpha.Bool> future =
                new AgonesFuture<>(AgonesMappers.ALPHA_BOOL_MAPPER);

        Alpha.PlayerID playerID = Alpha.PlayerID
                .newBuilder()
                .setPlayerID(playerId)
                .build();

        this.client.playerDisconnect(playerID, future);

        return future;
    }

    /**
     * Updates the {@link AgonesGameServer.Status.PlayerStatus#getCapacity()} value
     * with a new capacity
     *
     * @param count new player capacity
     * @return void
     */
    public CompletableFuture<Void> setPlayerCapacity(long count) {
        AgonesFuture<Void, Alpha.Empty> future = new AgonesFuture<>();

        Alpha.Count protoCount = Alpha.Count
                .newBuilder()
                .setCount(count)
                .build();

        this.client.setPlayerCapacity(protoCount, future);

        return future;
    }

    /**
     * Retrieves the current player capacity.
     * This is always accurate from what has been set through this SDK,
     * even if the value has yet to be updated on the {@link AgonesGameServer} status resource.
     *
     * @apiNote If {@link AgonesGameServer.Status.PlayerStatus#getCapacity()}
     * is set manually through the Kubernetes API,
     * use {@link AgonesSDK#getGameServer()} or {@link AgonesSDK#watchGameServer(BiConsumer)}
     * instead to view this value.
     *
     * @return current player capacity
     */
    public CompletableFuture<Long> getPlayerCapacity() {
        AgonesFuture<Long, Alpha.Count> future =
                new AgonesFuture<>(AgonesMappers.ALPHA_COUNT_MAPPER);

        this.client.getPlayerCapacity(Alpha.Empty.getDefaultInstance(), future);

        return future;
    }

    /**
     * Retrieves the current player capacity.
     * This is always accurate from what has been set through this SDK,
     * even if the value has yet to be updated on the {@link AgonesGameServer} status resource.
     *
     * @apiNote If {@link AgonesGameServer.Status.PlayerStatus#getPlayers()} is set manually through the Kubernetes API,
     * use {@link AgonesSDK#getGameServer()} or {@link AgonesSDK#watchGameServer(BiConsumer)}
     * instead to retrieve the current player count.
     *
     * @return current player count
     */
    public CompletableFuture<Long> getPlayerCount() {
        AgonesFuture<Long, Alpha.Count> future =
                new AgonesFuture<>(AgonesMappers.ALPHA_COUNT_MAPPER);

        this.client.getPlayerCount(Alpha.Empty.getDefaultInstance(), future);

        return future;
    }

    /**
     * Returns if the {@code playerId} is currently connected to the {@link AgonesGameServer}.
     * This is always accurate from what has been set through this SDK,
     * even if the value has yet to be updated on the {@link AgonesGameServer} status resource.
     *
     * @apiNote If {@link AgonesGameServer.Status.PlayerStatus#getPlayers()} is set manually through the Kubernetes API,
     * use {@link AgonesSDK#getGameServer()} or {@link AgonesSDK#watchGameServer(BiConsumer)}
     * instead to determine connected status.
     *
     * @param playerId player id to check
     * @return true if playerId is currently connected, false otherwise
     */
    public CompletableFuture<Boolean> isPlayerConnected(String playerId) {
        AgonesFuture<Boolean, Alpha.Bool> future =
                new AgonesFuture<>(AgonesMappers.ALPHA_BOOL_MAPPER);

        Alpha.PlayerID playerID = Alpha.PlayerID
                .newBuilder()
                .setPlayerID(playerId)
                .build();

        this.client.isPlayerConnected(playerID, future);

        return future;
    }

    /**
     * Returns the list of the currently connected player ids.
     * This is always accurate from what has been set through this SDK,
     * even if the value has yet to be updated on the {@link AgonesGameServer} status resource.
     *
     * @apiNote If {@link AgonesGameServer.Status.PlayerStatus#getPlayers()} is set manually through the Kubernetes API,
     * use {@link AgonesSDK#getGameServer()} or {@link AgonesSDK#watchGameServer(BiConsumer)}
     * instead to list the connected players.
     *
     * @return currently connected player id list
     */
    public CompletableFuture<List<String>> getConnectedPlayers() {
        AgonesFuture<List<String>, Alpha.PlayerIDList> future =
                new AgonesFuture<>(AgonesMappers.ALPHA_PLAYER_ID_LIST_MAPPER);

        this.client.getConnectedPlayers(Alpha.Empty.getDefaultInstance(), future);

        return future;
    }
}
