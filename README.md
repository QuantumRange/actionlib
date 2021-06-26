# ActionLib
[![Java Maven Build](https://github.com/QuantumRange/ActionLib/actions/workflows/maven.yml/badge.svg)](https://github.com/QuantumRange/ActionLib/actions/workflows/maven.yml)

<!-- # What is it exactly? TODO --> 

# Use Actions
First an implementation of the `ActionManager` is required.
This must be accessible at best from everywhere.
An example over the main function:
````java
public static final ActionManager <name> = <implementation>.

public static void main(String[] args) {
	/* ... */
}
````
For the implementation I recommend having a look at the chapter `implementations`.

## ActionManager
### Implementations
Currently, (version 2.4.x) there are only two implementations.
If more extensions of the ActionManager are needed, please open a new issue.

#### MultiThreadManager
The `MultiThreadManager` is useful for locale tasks on the device, as it handles as many actions as possible. 
It is only limited by the processor threads
*(which is best not to exceed the number of threads on your computer)*.
````java
ActionManager manager = new MultiThreadManager(); // Use the maximum number of available threads.
ActionManager manager = new MultiThreadManager(8); // Uses the number of threads specified.
ActionManager manager = new MultiThreadManager(.5f); // Uses how much percent of the available threads should be used.
````

#### RateLimitedThreadManager
The `RateLimitedThreadManager` is useful for requests to the internet where you want to avoid too many requests.
There is a separate action implementation that should be used for this.
```java
ActionManager manager = new RateLimitedThreadManager(); // Use the maximum number of available threads.
ActionManager manager = new RateLimitedThreadManager(8); // Uses the number of threads specified.
ActionManager manager = new RateLimitedThreadManager(.5f); // Uses how much percent of the available threads should be used.

manager.registerRateLimit(0, 500); // For example, RateID 0 can be a request to Google.com.
manager.registerRateLimit(1, 1500); // RateID 1 can, for example, send a request to Bing.com.

Action<String> exampleAction = new RateLimitedAction(manager, /* id */0, throwable -> /* Sending Request*/);
exampleAction.queue();
```
