import java.util.concurrent.locks.LockSupport;

public final class ParkUnpark {

	private volatile boolean _waiting;
	private volatile boolean _done;

	private void awaitUnpark(int i) {
		_waiting = true;
		System.out.println("Parking " + i);  // <-- MOVING THIS LINE ONE UP MAKES TEST PASS
		LockSupport.park(this);
		System.out.println("Unparked " + i);
		_waiting = false;
		_done = true;
	}

	public void main(int iterations) {
		Thread t = Thread.ofPlatform().start(() -> {
			for(int i = 0; i < iterations; ++i)
				awaitUnpark(i);
		});

		for(;;) {
			_done = false;
			while(!_waiting)
				if(!t.isAlive())
					return;
			System.out.println("Unparking");
			LockSupport.unpark(t);
			while(!_done);
		}
	}

	public static void main(String... args) {
		new ParkUnpark().main(args.length == 0 ? 100_000 : Integer.parseInt(args[0]));
	}

}
