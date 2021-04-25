/**
 * plays a sound after a certain length of time
 * @author Gabriel Mulcahy
 * @since 13-02-2019
 */
public class Timer implements Runnable {

    private long time;


    @Override
    public void run() {
        try {
            Thread.sleep(time);
            SoundPlayer.playSound("res/timer.wav");
        } catch (InterruptedException e) {
            System.err.println("Timer Interrupted: " + e.getMessage());
        }
    }

    /**
     * set the time for the timer
     *
     * @param minutes length of timer
     */
    public void setTime(long minutes) {
        time = minutes * 60000; //convert from minutes to milliseconds
    }
}

