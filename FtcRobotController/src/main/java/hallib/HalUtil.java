package hallib;

/**
 * This class implements some useful platform dependent utility methods.
 */
public class HalUtil
{
    /**
     * This method returns the current time in seconds.
     *
     * @return current time in seconds.
     */
    public static double getCurrentTime()
    {
        return System.currentTimeMillis()/1000.0;
    }   //getCurrentTime

    /**
     * This method returns the current time in msec.
     *
     * @return current time in msec.
     */
    public static long getCurrentTimeMillis()
    {
        return System.currentTimeMillis();
    }   //getCurrentTimeMillis

    /**
     * This method puts the current thread to sleep for the given time in msec.
     * It handles InterruptException where it recalculates the remaining time
     * and calls sleep again repeatedly until the specified sleep time has past.
     *
     * @param sleepTime specifies sleep time in msec.
     */
    public static void sleep(long sleepTime)
    {
        long wakeupTime = System.currentTimeMillis() + sleepTime;

        while (sleepTime > 0)
        {
            try
            {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException e)
            {
            }
            sleepTime = wakeupTime - System.currentTimeMillis();
        }
    }   //sleep

}   //class HalUtil
