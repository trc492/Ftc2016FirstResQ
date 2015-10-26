package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.Gamepad;

import hallib.FtcGamepad;
import hallib.FtcRobot;
import hallib.HalDashboard;
import hallib.HalSpeedController;
import trclib.TrcDriveBase;

public class FtcTeleOp extends FtcRobot implements FtcGamepad.ButtonHandler
{
    private HalDashboard dashboard;
    private FtcGamepad driverGamepad;
    private FtcGamepad operatorGamepad;
    private HalSpeedController leftFrontWheel;
    private HalSpeedController rightFrontWheel;
    private HalSpeedController leftRearWheel;
    private HalSpeedController rightRearWheel;
    private TrcDriveBase driveBase;
    private Elevator elevator;
    private Chainsaw chainsaw;
    private ClimberRelease climberRelease;
    private CattleGuard cattleGuard;

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

        leftFrontWheel = new HalSpeedController(hardwareMap.dcMotor.get("leftFrontWheel"));
        rightFrontWheel = new HalSpeedController(hardwareMap.dcMotor.get("rightFrontWheel"));
        leftRearWheel = new HalSpeedController(hardwareMap.dcMotor.get("leftRearWheel"));
        rightRearWheel = new HalSpeedController(hardwareMap.dcMotor.get("rightRearWheel"));
        leftFrontWheel.setInverted(true);
        leftRearWheel.setInverted(true);

        driveBase = new TrcDriveBase(
                leftFrontWheel,
                leftRearWheel,
                rightFrontWheel,
                rightRearWheel,
                null,
                null);
        elevator = new Elevator();
        chainsaw = new Chainsaw();
        climberRelease = new ClimberRelease();
        cattleGuard = new CattleGuard();
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
        //
        // DriveBase subsystem.
        //
        double leftPower  = driverGamepad.getLeftStickY(true);
        double rightPower = driverGamepad.getRightStickY(true);
        driveBase.tankDrive(leftPower, rightPower);
        dashboard.displayPrintf(1, "leftPower = %f", leftPower);
        dashboard.displayPrintf(2, "rightPower = %f", rightPower);
        //
        // Elevator subsystem.
        //
        double elevatorPower = operatorGamepad.getLeftStickY(true);
        dashboard.displayPrintf(3, "elevatorPower = %f", elevatorPower);
        //
        // Chainsaw subsystem.
        //
        double chainsawPower = operatorGamepad.getRightStickY(true);
        dashboard.displayPrintf(4, "chainsawPower = %f", chainsawPower);
    }   //runPeriodic

    @Override
    public void runContinuous()
    {
    }   //runContinuous

    //
    // Implemnts FtcGamepad.ButtonHandler interface.
    //

    @Override
    public void gamepadButtonEvent(FtcGamepad gamepad, final int btnMask, final boolean pressed)
    {
        dashboard.displayPrintf(8, "%s: %04x->%s",
                gamepad.toString(), btnMask, pressed? "Pressed": "Released");
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
