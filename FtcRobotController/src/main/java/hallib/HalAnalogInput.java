package hallib;

import com.qualcomm.robotcore.hardware.AnalogInput;

public class HalAnalogInput
{
    AnalogInput analogInput;

    public HalAnalogInput(AnalogInput analogInput)
    {
        this.analogInput = analogInput;
    }   //HalAnalogInput

    public int getValue()
    {
        return analogInput.getValue();
    }   //getVoltage

}   //class HalAnalogInput
