package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.ColorSensor;

import hallib.FtcGamepad;
import hallib.FtcRobot;
import hallib.HalDashboard;

//import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class FtcTeleOp extends FtcRobot implements FtcGamepad.ButtonHandler
{
    private HalDashboard dashboard;
    private FtcGamepad driverGamepad;
    private FtcGamepad operatorGamepad;
    private DcMotor leftFrontWheel;
    private DcMotor rightFrontWheel;
    private DcMotor leftRearWheel;
    private DcMotor rightRearWheel;
//    private ColorSensor colorSensor;
    private OpticalDistanceSensor distanceSensor;
    private TouchSensor touchSensor;
    private long prevPeriodicTime;
    private long prevContinuousTime;

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

//        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        distanceSensor = hardwareMap.opticalDistanceSensor.get("distanceSensor");
        touchSensor = hardwareMap.touchSensor.get("touchSensor");

        prevPeriodicTime = System.currentTimeMillis();
        prevContinuousTime = prevPeriodicTime;
//        dashboard.displayPrintf(7, "Init completed");
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
        long currTime = System.currentTimeMillis();
        dashboard.displayPrintf(1, "Periodic: %d", currTime - prevPeriodicTime);
        prevPeriodicTime = currTime;
//        double leftPower  = driverGamepad.getLeftStickY(true);
//        double rightPower = driverGamepad.getRightStickY(true);
        double leftPower = -gamepad1.left_stick_y;
        double rightPower = -gamepad1.right_stick_y;
        dashboard.displayPrintf(3, "leftPower  = %f", leftPower);
        dashboard.displayPrintf(4, "rightPower = %f", rightPower);
        dashboard.displayPrintf(5, "touch = %s", touchSensor.isPressed()? "Pressed": "Released");
        dashboard.displayPrintf(6, "distance = %f", distanceSensor.getLightDetected());

        // write the values to the motors
        leftFrontWheel.setPower(leftPower);
        rightFrontWheel.setPower(rightPower);
        leftRearWheel.setPower(leftPower);
        rightRearWheel.setPower(rightPower);

    }   //runPeriodic

    @Override
    public void runContinuous()
    {
        long currTime = System.currentTimeMillis();
        dashboard.displayPrintf(2, "Continuous: %d", currTime - prevContinuousTime);
        prevContinuousTime = currTime;

    }   //runContinuous

    //
    // Implemnts FtcGamepad interface.
    //

    @Override
    public void gamepadButtonEvent(FtcGamepad gamepad, final int btnMask, final boolean pressed)
    {
        if (gamepad == driverGamepad)
        {
//            dashboard.displayPrintf(5, "Driver[%d] = %s", btnMask, Boolean.toString(pressed));
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

}   //class FtcTeleOp
