package hallib;

public class HalUtil
{
    public static double getCurrentTime()
    {
        return System.currentTimeMillis()/1000.0;
    }   //getCurrentTime

    public static long getCurrentTimeMillis()
    {
        return System.currentTimeMillis();
    }   //getCurrentTimeMillis

    public static void sleep(long sleepTime)
    {
        long currTime = System.currentTimeMillis();

        while (sleepTime > 0)
        {
            long wakeupTime = currTime + sleepTime;

            try
            {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException e)
            {
                currTime = System.currentTimeMillis();
                sleepTime = wakeupTime - currTime;
            }
        }
    }   //sleep

}   //class HalUtil
