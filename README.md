# agones-sdk-4j
Thin asynchronous java wrapper of the gRPC generated client for [agones client sdk](https://agones.dev/site/docs/guides/client-sdks/)

## Example usage
```java
AgonesSDK sdk = AgonesSDK.create();

sdk.ready()
    .whenComplete((empty, error) -> {
        if(error != null) {
            // Handle error
        } else {
            // Successfully marked this game server as ready
        }
    });

try {
    AgonesGameServer gameServer = sdk.getGameServer()
        .get(5, TimeUnit.SECONDS); // Synchronous usage
        
    String state = gameServer.getStatus().getState();
    //Do stuff
} catch (Exception e) {
    // Handle exception
}
        
sdk.health(); // Health ping
```