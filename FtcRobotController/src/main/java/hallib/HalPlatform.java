package hallib;

public class HalPlatform
{
    private static Object platformObj = null;

    public HalPlatform(Object platformObj)
    {
        this.platformObj = platformObj;
    }   //HalPlatform

    public static Object getPlatformObject()
    {
        return platformObj;
    }   //getPlatformObject

    public static double getCurrentTime()
    {
        return System.currentTimeMillis()/1000.0;
    }   //getCurrentTime

    public static long getCurrentTimeMillis()
    {
        return System.currentTimeMillis();
    }   //getCurrentTimeMillis

}   //class HalPlatform
