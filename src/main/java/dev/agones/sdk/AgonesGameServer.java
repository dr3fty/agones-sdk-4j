package dev.agones.sdk;

import dev.agones.sdk.Sdk;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Backing Game Server configuration and status
 */
public class AgonesGameServer {

    private final ObjectMeta objectMeta;
    private final Spec spec;
    private final Status status;

    private AgonesGameServer(ObjectMeta objectMeta, Spec spec, Status status) {
        this.objectMeta = objectMeta;
        this.spec = spec;
        this.status = status;
    }

    public ObjectMeta getObjectMeta() {
        return objectMeta;
    }

    public Spec getSpec() {
        return spec;
    }

    public Status getStatus() {
        return status;
    }

    static AgonesGameServer fromProto(Sdk.GameServer protoGameServer) {
        return new AgonesGameServer(
                ObjectMeta.fromProto(protoGameServer.getObjectMeta()),
                Spec.fromProto(protoGameServer.getSpec()),
                Status.fromProto(protoGameServer.getStatus())
        );
    }

    /**
     * K8s ObjectMeta resource
     */
    public static class ObjectMeta {
        private final String name;
        private final String namespace;
        private final String uid;
        private final String resourceVersion;
        private final long generation;

        private final long creationTimestamp;
        private final long deletionTimestamp;

        private final Map<String, String> annotations;
        private final Map<String, String> labels;

        private ObjectMeta(
                String name,
                String namespace,
                String uid,
                String resourceVersion,
                long generation,
                long creationTimestamp,
                long deletionTimestamp,
                Map<String, String> annotations,
                Map<String, String> labels) {
            this.name = name;
            this.namespace = namespace;
            this.uid = uid;
            this.resourceVersion = resourceVersion;
            this.generation = generation;
            this.creationTimestamp = creationTimestamp;
            this.deletionTimestamp = deletionTimestamp;
            this.annotations = annotations;
            this.labels = labels;
        }

        public String getName() {
            return name;
        }

        public String getNamespace() {
            return namespace;
        }

        public String getUid() {
            return uid;
        }

        public String getResourceVersion() {
            return resourceVersion;
        }

        public long getGeneration() {
            return generation;
        }

        /**
         * @return creation timestamp in seconds
         */
        public long getCreationTimestamp() {
            return creationTimestamp;
        }

        /**
         * @return deletion timestamp in seconds
         */
        public long getDeletionTimestamp() {
            return deletionTimestamp;
        }

        public Map<String, String> getAnnotations() {
            return annotations;
        }

        public Map<String, String> getLabels() {
            return labels;
        }

        static ObjectMeta fromProto(Sdk.GameServer.ObjectMeta protoGameServer) {
            return new ObjectMeta(
                    protoGameServer.getName(),
                    protoGameServer.getNamespace(),
                    protoGameServer.getUid(),
                    protoGameServer.getResourceVersion(),
                    protoGameServer.getGeneration(),
                    protoGameServer.getCreationTimestamp(),
                    protoGameServer.getDeletionTimestamp(),
                    protoGameServer.getAnnotationsMap(),
                    protoGameServer.getLabelsMap()
            );
        }
    }

    public static class Spec {

        private final Health health;

        public Spec(Health health) {
            this.health = health;
        }

        public Health getHealth() {
            return health;
        }

        static Spec fromProto(Sdk.GameServer.Spec protoSpec) {
            return new Spec(
                    Health.fromProto(protoSpec.getHealth())
            );
        }

        public static class Health {
            private final boolean disabled;
            private final int periodSeconds;
            private final int failureThreshold;
            private final int initialDelaySeconds;

            private Health(
                    boolean disabled,
                    int periodSeconds,
                    int failureThreshold,
                    int initialDelaySeconds
            ) {
                this.disabled = disabled;
                this.periodSeconds = periodSeconds;
                this.failureThreshold = failureThreshold;
                this.initialDelaySeconds = initialDelaySeconds;
            }

            public boolean isDisabled() {
                return disabled;
            }

            public int getPeriodSeconds() {
                return periodSeconds;
            }

            public int getFailureThreshold() {
                return failureThreshold;
            }

            public int getInitialDelaySeconds() {
                return initialDelaySeconds;
            }

            static Health fromProto(Sdk.GameServer.Spec.Health protoHealth) {
                return new Health(
                        protoHealth.getDisabled(),
                        protoHealth.getPeriodSeconds(),
                        protoHealth.getFailureThreshold(),
                        protoHealth.getInitialDelaySeconds()
                );
            }
        }
    }

    public static class Status {

        private final String state;
        private final String address;

        private final List<Port> ports;

        private final PlayerStatus players;

        private Status(String state, String address, List<Port> ports, PlayerStatus players) {
            this.state = state;
            this.address = address;
            this.ports = ports;
            this.players = players;
        }

        public String getState() {
            return state;
        }

        public String getAddress() {
            return address;
        }

        public List<Port> getPorts() {
            return ports;
        }

        public PlayerStatus getPlayers() {
            return players;
        }

        static Status fromProto(Sdk.GameServer.Status protoStatus) {
            return new Status(
                    protoStatus.getState(),
                    protoStatus.getAddress(),
                    protoStatus.getPortsList().stream().map(Port::fromProto).collect(Collectors.toList()),
                    PlayerStatus.fromProto(protoStatus.getPlayers())
            );
        }

        public static class Port {
            private final String name;
            private final int port;

            private Port(String name, int port) {
                this.name = name;
                this.port = port;
            }

            public String getName() {
                return name;
            }

            public int getPort() {
                return port;
            }

            public static Port fromProto(Sdk.GameServer.Status.Port protoPort) {
                return new Port(
                        protoPort.getName(),
                        protoPort.getPort()
                );
            }
        }

        public static class PlayerStatus {
            private final long count;
            private final long capacity;

            private final List<String> ids;

            private PlayerStatus(long count, long capacity, List<String> ids) {
                this.count = count;
                this.capacity = capacity;
                this.ids = ids;
            }

            public long getCount() {
                return count;
            }

            public long getCapacity() {
                return capacity;
            }

            public List<String> getIds() {
                return ids;
            }

            static PlayerStatus fromProto(Sdk.GameServer.Status.PlayerStatus protoPlayerStatus) {
                return new PlayerStatus(
                        protoPlayerStatus.getCount(),
                        protoPlayerStatus.getCapacity(),
                        protoPlayerStatus.getIdsList()
                );
            }
        }
    }
}
