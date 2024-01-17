package jpacman.util;

public class Timer
{
    public static final int HALF_SECOND = 500;
    public static final int ONE_SECOND = 1000;
    public static final int ONE_AND_A_HALF_SECOND = 1500;
    public static final int TWO_SECONDS = 2000;
    public static final int TWO_AND_A_HALF_SECONDS = 2500;
    public static final int THREE_SECONDS = 3000;
    public static final int FOUR_SECONDS = 4000;
    public static final int NINE_SECONDS = 9000;

    private long timer;
    private boolean started;
    private boolean paused;
    private int timeElapsedInMilliseconds;

    public Timer()
    {
	started = false;
	paused = false;
	timeElapsedInMilliseconds = 0;
    }

    public void countTimeInMilliseconds()
    {
	if (!started) {
	    timer = System.currentTimeMillis();
	    started = true;
	}

	if (System.currentTimeMillis() - timer > 100) { // precision of 100 milliseconds
	    if (!paused) {
		timeElapsedInMilliseconds += 100;
	    }
	    timer += 100;
	}
    }

    public void countTimeInMillisecondsPrecise()
    {
	if (!started) {
	    timer = System.currentTimeMillis();
	    started = true;
	}

	if (System.currentTimeMillis() - timer > 10) { // precision of 10 milliseconds
	    if (!paused) {
		timeElapsedInMilliseconds += 10;
	    }
	    timer += 10;
	}
    }

    public void reset()
    {
	started = false;
	paused = false;
	timeElapsedInMilliseconds = 0;
    }

    public boolean isStarted()
    {
	return started;
    }

    public void setStarted(boolean started)
    {
	this.started = started;
    }

    public boolean isPaused()
    {
	return paused;
    }

    public void setPaused(boolean paused)
    {
	this.paused = paused;
    }

    public int getTimeElapsedInMilliseconds()
    {
	return timeElapsedInMilliseconds;
    }

    public void setTimeElapsedInMilliseconds(int timeElapsedInMilliseconds)
    {
	this.timeElapsedInMilliseconds = timeElapsedInMilliseconds;
    }
}
