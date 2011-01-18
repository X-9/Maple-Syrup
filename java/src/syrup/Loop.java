package syrup;

abstract interface Idle {
	abstract void move();
}

abstract interface Render {
	abstract void display();
}

public class Loop implements Runnable {
	static private final int UPDATE_RATE = 50;
	static private final int UPDATE_PERIOD = 1000/UPDATE_RATE;
	
	private final Idle idle;
	private final Render render;
	private Thread thread;
	
	public Loop(final Idle i, final Render r) {
		idle = i; render = r;
		thread = new Thread(this);
	}
	
	public void start() {
		thread.start();
	}
	
	@Override
	public void run() {
		
		long tick = System.currentTimeMillis();
		long sleep = 0;
		
		for (;;) {
			idle.move();
			render.display();
			tick += UPDATE_PERIOD;
			sleep = tick - System.currentTimeMillis();
			if (sleep >= 0)	try { Thread.sleep(sleep); } catch (InterruptedException e) { }
		}
	}
}
