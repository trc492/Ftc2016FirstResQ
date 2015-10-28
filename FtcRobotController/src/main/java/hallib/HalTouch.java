package hallib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class HalTouch
{
    private String instanceName;
    private HardwareMap hardwareMap;
    private TouchSensor touchSensor;

    public HalTouch(String instanceName)
    {
        this.instanceName = instanceName;
        hardwareMap = (HardwareMap)HalPlatform.getPlatformObject();
        this.touchSensor = hardwareMap.touchSensor.get(instanceName);
    }   //HalTouch

    public boolean isPressed()
    {
        return touchSensor.isPressed();
    }   //isPressed

    public String toString()
    {
        return instanceName;
    }   //toString

}   //class HalTouch
