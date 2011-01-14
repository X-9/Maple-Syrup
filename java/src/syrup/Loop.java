package syrup;

abstract interface Idle {
	abstract void move();
}

abstract interface Render {
	abstract void display();
}

public class Loop implements Runnable {
	static private final int DELAY = 10;
	
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
		for (;;) {
			long current = System.currentTimeMillis();
			idle.move();
			render.display();
			long diff = DELAY - (System.currentTimeMillis()-current);
			diff = (diff < 2) ? 2 : diff;
			try {
				Thread.sleep(diff);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
