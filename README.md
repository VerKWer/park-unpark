# Lost Unparks in Gradle Test Runner
Minimal example of a lost thread unpark when running in a Gradle test runner.
It runs a single background thread that repeatedly parks and gets unparked by the main thread.

Waiting is done by busy waiting, leading to high CPU usage.
This only reason for this is to rule out any unexpected interactions between
thread (un)parks with more appropriate measures like using a `CountDownLatch`,
because that also uses thread parking and unparking internally.


## Main Problem
The project contains a single test, which simply calls the application's `main` method.
One would expect this to lead to the same result as just running it (e.g. using `./gradlew run`).
However, the test quickly hangs. The output looks something like this:
```
Unparking
Parking 0
Unparked 0
Unparking
Parking 1
Unparked 1
Unparking
Parking 2
```
(the actual iteration where the test hangs varies).

From the looks of it, an unpark of the background thread is lost and the main thread
blocks, waiting for the (parked) background thread to wake up.

To add some extra weirdness, moving the print statement at `ParkUnpark:10` one line up
(or removing it entirely) makes the test pass.


## Secondary Problem
Running the main class directly under Java 25 with a high iteration count using
```bash
java src/main/java/ParkUnpark.java 1000000
```
completes just fine.

Running it through Gradle via
```bash
./gradlew run --args 1000000
```
slows down and eventually hangs. Lower iteration counts are ok.
Disabling the Gradle daemon doesn't help.
Presumably, this is due to some serialisation overhead when printing to STDOUT
(which Gradle messes with). Removing all print statements makes everything
work just fine.
