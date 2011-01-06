package syrup;

abstract interface Idle {
	abstract void move();
}

abstract interface Render {
	abstract void display();
}

public class Loop implements Runnable {
	static private final int DELAY = 50;
	
	private Idle idle;
	private Render render;
	
	public Loop(Idle i, Render r) {
		idle = i; render = r;
		new Thread(this).start();
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
