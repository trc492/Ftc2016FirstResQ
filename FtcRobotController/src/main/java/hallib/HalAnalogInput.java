package hallib;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class HalAnalogInput
{
    String instanceName;
    HardwareMap hardwareMap;
    AnalogInput analogInput;

    public HalAnalogInput(String instanceName)
    {
        this.instanceName = instanceName;
        hardwareMap = (HardwareMap)HalPlatform.getPlatformObject();
        this.analogInput = hardwareMap.analogInput.get(instanceName);
    }   //HalAnalogInput

    public int getValue()
    {
        return analogInput.getValue();
    }   //getValue

    public String toString()
    {
        return instanceName;
    }   //toString

}   //class HalAnalogInput
