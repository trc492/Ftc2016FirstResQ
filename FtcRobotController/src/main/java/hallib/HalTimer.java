package hallib;

public class HalTimer
{
    public static double getCurrentTime()
    {
        return System.currentTimeMillis()/1000.0;
    }   //getCurrentTime

    public static long getCurrentTimeMillis()
    {
        return System.currentTimeMillis();
    }   //getCurrentTimeMillis

}   //class HalTimer
