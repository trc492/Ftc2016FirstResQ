package com.qualcomm.ftcrobotcontroller.opmodes;

import hallib.FtcGamepad;
import hallib.FtcRobot;
import hallib.HalDashboard;

import com.qualcomm.robotcore.hardware.DcMotor;

public class FtcTeleOp extends FtcRobot implements FtcGamepad.ButtonHandler
{
    HalDashboard dashboard;
    FtcGamepad driverGamepad;
    FtcGamepad operatorGamepad;
    DcMotor leftFrontWheel;
    DcMotor rightFrontWheel;
    DcMotor leftRearWheel;
    DcMotor rightRearWheel;
    long prevPeriodicTime;
    long prevContinuousTime;

    //
    // Implements FtcRobot abstract methods.
    //

    @Override
    public void robotInit()
    {
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

        prevPeriodicTime = System.currentTimeMillis();
        prevContinuousTime = prevPeriodicTime;
    }   //robotInit

    @Override
    public void startMode()
    {

    }   //startMode

    @Override
    public void runPeriodic()
    {
        long currTime = System.currentTimeMillis();
        dashboard.displayPrintf(0, "Periodic: %d", currTime - prevPeriodicTime);
        prevPeriodicTime = currTime;
//        double leftPower  = driverGamepad.getLeftStickY(true);
//        double rightPower = driverGamepad.getRightStickY(true);
        double leftPower = -gamepad1.left_stick_y;
        double rightPower = -gamepad1.right_stick_y;
        dashboard.displayPrintf(2, "leftPower  = ", leftPower);
        dashboard.displayPrintf(3, "rightPower = ", rightPower);

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
        dashboard.displayPrintf(1, "Continuous: %d", currTime - prevContinuousTime);
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
            dashboard.displayPrintf(4, "Driver[%d] = %s", btnMask, Boolean.toString(pressed));
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
            dashboard.displayPrintf(5, "Operator[%d] = %s", btnMask, Boolean.toString(pressed));
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
