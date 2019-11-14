package fr.univlille.modelisation;

public class Physique implements Runnable {

	private Univers univers;
	private double temps = 0;

	public Physique(Univers univers) {
		this.univers = univers;
	}
	
	private boolean running = true;
    private boolean paused = false;
    private final Object pauseLock = new Object();

    @Override
    public void run() {
        while (running) {
            synchronized (pauseLock) {
                if (!running) {
                    break;
                }
                if (paused) {
                    try {
                        synchronized (pauseLock) {
                            pauseLock.wait();
                        }
                    } catch (InterruptedException ex) {
                        break;
                    }
                    if (!running) {
                        break;
                    }
                }
            }
            univers.update();
			temps += univers.getDt();
			try {
				Thread.sleep((int) univers.getDt());
			} catch (InterruptedException e) {}
        }
    }

    public void stop() {
        running = false;
        resume();
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }
	
	public double getTime() {
		return temps;
	}
}
