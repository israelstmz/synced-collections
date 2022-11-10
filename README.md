# Synced In-Memory Cache

A fast in-memory cached collection, synced with a persistent/centralized one. 

Assuming existence of a persistent/centralized collection in a DB of any sort - this library provides a mirrored, fast, in-memory cached collection, 
backed and synced by that DB - Enjoying both the fastest data access ***and*** the persistent, distributed, centralized nature of the DB.


This cache is read-only and designed for optimized, small-cache "get/contains".

## Usage

#### Using default creator
```java
public class Example {

    public static void main(String[] args) {
        // creating an instance of SyncedCollection
        SyncCollectionSupplier<String> supplier = webClient.fetchCollection();
        Collection<String> syncedCollection = SyncedCollection.createAndSync(supplier);

        // using it
        assert syncedCollection.contains("<>");
    }

}
```


#### Using custom builder
```java
public class Example {

    public static void main(String[] args) {
        // building an instance of SyncedCollection
        SyncCollectionSupplier<String> supplier = webClient.fetchCollection();
        Collection<String> syncedCollection = SyncedCollection.build(supplier)
                                                              .interval(Duration.ofMinutes(1))
                                                              .buildAndSync();

        // using it
        assert syncedCollection.contains("item");
    }

}
```