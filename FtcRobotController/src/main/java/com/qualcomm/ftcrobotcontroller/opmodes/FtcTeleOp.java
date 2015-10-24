package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import hallib.FtcGamepad;
import hallib.FtcRobot;
import hallib.HalDashboard;

public class FtcTeleOp extends FtcRobot implements FtcGamepad.ButtonHandler
{
    private HalDashboard dashboard;
    private FtcGamepad driverGamepad;
    private FtcGamepad operatorGamepad;
    private DcMotor leftFrontWheel;
    private DcMotor rightFrontWheel;
    private DcMotor leftRearWheel;
    private DcMotor rightRearWheel;
    private ColorSensor colorSensor;
    private OpticalDistanceSensor distanceSensor;
    private TouchSensor touchSensor;

    //
    // Implements FtcRobot abstract methods.
    //

    @Override
    public void robotInit()
    {
        hardwareMap.logDevices();
        dashboard = HalDashboard.getInstance();

        driverGamepad = new FtcGamepad("DriverGamepad", gamepad1, this);
        operatorGamepad = new FtcGamepad("OperatorGamepad", gamepad2, this);
        driverGamepad.setYInverted(true);
        operatorGamepad.setYInverted(true);

        leftFrontWheel = hardwareMap.dcMotor.get("leftFrontWheel");
        rightFrontWheel = hardwareMap.dcMotor.get("rightFrontWheel");
        leftRearWheel = hardwareMap.dcMotor.get("leftRearWheel");
        rightRearWheel = hardwareMap.dcMotor.get("rightRearWheel");
        leftFrontWheel.setDirection(DcMotor.Direction.REVERSE);
        leftRearWheel.setDirection(DcMotor.Direction.REVERSE);

        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        colorSensor.enableLed(false);
        distanceSensor = hardwareMap.opticalDistanceSensor.get("distanceSensor");
        touchSensor = hardwareMap.touchSensor.get("touchSensor");
    }   //robotInit

    @Override
    public void startMode()
    {
    }   //startMode

    @Override
    public void stopMode()
    {
    }   //stopMode

    @Override
    public void runPeriodic()
    {
        double leftPower  = driverGamepad.getLeftStickY(true);
        double rightPower = driverGamepad.getRightStickY(true);
        dashboard.displayPrintf(1, "leftPower  = %f", leftPower);
        dashboard.displayPrintf(2, "rightPower = %f", rightPower);
        dashboard.displayPrintf(3, "color[%d,%d,%d]", colorSensor.red(), colorSensor.green(), colorSensor.blue());
        dashboard.displayPrintf(4, "distance = %f", distanceSensor.getLightDetected());
        dashboard.displayPrintf(5, "touch = %s", touchSensor.isPressed()? "Pressed": "Released");

        // write the values to the motors
        leftFrontWheel.setPower(leftPower);
        rightFrontWheel.setPower(rightPower);
        leftRearWheel.setPower(leftPower);
        rightRearWheel.setPower(rightPower);
    }   //runPeriodic

    @Override
    public void runContinuous()
    {
    }   //runContinuous

    //
    // Implemnts FtcGamepad interface.
    //

    @Override
    public void gamepadButtonEvent(FtcGamepad gamepad, final int btnMask, final boolean pressed)
    {
        dashboard.displayPrintf(6, "%s: %04x->%s", gamepad.toString(), btnMask, Boolean.toString(pressed));
        if (gamepad == driverGamepad)
        {
            switch (btnMask)
            {
                case FtcGamepad.GAMEPAD_A:
                    break;

                case FtcGamepad.GAMEPAD_B:
                    break;

                case FtcGamepad.GAMEPAD_X:
                    break;

                case FtcGamepad.GAMEPAD_Y:
                    break;
            }
        }
        else if (gamepad == operatorGamepad)
        {
//            dashboard.displayPrintf(6, "Operator[%d] = %s", btnMask, Boolean.toString(pressed));
            switch (btnMask)
            {
                case FtcGamepad.GAMEPAD_A:
                    break;

                case FtcGamepad.GAMEPAD_B:
                    break;

                case FtcGamepad.GAMEPAD_X:
                    break;

                case FtcGamepad.GAMEPAD_Y:
                    break;
            }
        }
    }   //gamepadButtonEvent

    public Gamepad getGamepad(String name)
    {
        if (name.equals("DriverGamepad"))
        {
            return gamepad1;
        }
        else if (name.equals("OperatorGamepad"))
        {
            return gamepad2;
        }
        else
        {
            return null;
        }
    }   //??? TEMP
}   //class FtcTeleOp
