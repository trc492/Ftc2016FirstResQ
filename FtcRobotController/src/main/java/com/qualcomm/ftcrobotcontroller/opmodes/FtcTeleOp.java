package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.Gamepad;

import hallib.FtcDcMotor;
import hallib.FtcGamepad;
import hallib.FtcRobot;
import hallib.HalDashboard;
import hallib.HalPlatform;
import trclib.TrcBooleanState;
import trclib.TrcDriveBase;

public class FtcTeleOp extends FtcRobot implements FtcGamepad.ButtonHandler
{
    private HalPlatform platform;
    private HalDashboard dashboard;
    private FtcGamepad driverGamepad;
    private FtcGamepad operatorGamepad;
    private FtcDcMotor leftFrontWheel;
    private FtcDcMotor rightFrontWheel;
    private FtcDcMotor leftRearWheel;
    private FtcDcMotor rightRearWheel;
    private TrcDriveBase driveBase;
    private Chainsaw chainsaw;
    private Elevator elevator;
    private HangingHook hangingHook;
    private ClimberRelease leftArm;
    private ClimberRelease rightArm;
    private CattleGuard cattleGuard;
    private TrcBooleanState cattleGuardDeployed;

    //
    // Implements FtcRobot abstract methods.
    //

    @Override
    public void robotInit()
    {
        //
        // Initializing global objects.
        //
        hardwareMap.logDevices();
        platform = new HalPlatform(this);
        dashboard = HalDashboard.getInstance();
        //
        // Initializing Gamepads.
        //
        driverGamepad = new FtcGamepad("DriverGamepad", gamepad1, this);
        operatorGamepad = new FtcGamepad("OperatorGamepad", gamepad2, this);
        driverGamepad.setYInverted(true);
        operatorGamepad.setYInverted(true);
        //
        // DriveBase subsystem.
        //
        leftFrontWheel = new FtcDcMotor("leftFrontWheel");
        rightFrontWheel = new FtcDcMotor("rightFrontWheel");
        leftRearWheel = new FtcDcMotor("leftRearWheel");
        rightRearWheel = new FtcDcMotor("rightRearWheel");
        leftFrontWheel.setInverted(true);
        leftRearWheel.setInverted(true);

        driveBase = new TrcDriveBase(
                leftFrontWheel,
                leftRearWheel,
                rightFrontWheel,
                rightRearWheel,
                null,
                null);
        //
        // Chainsaw subsystem.
        //
        chainsaw = new Chainsaw();
        //
        // Elevator subsystem.
        //
        elevator = new Elevator();
        //
        // Hanging Hook subsystem.
        //
        hangingHook = new HangingHook();
        //
        // Climber Release subsystem.
        //
        leftArm = new ClimberRelease("leftArm");
        rightArm = new ClimberRelease("rightArm");
        //
        // Cattle Guard subsystem.
        //
        cattleGuard = new CattleGuard();
        cattleGuardDeployed = new TrcBooleanState("cattleGuardDeployed", false);
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
        double leftTriggerPower = driverGamepad.getLeftTrigger(true);
        double rightTriggerPower = driverGamepad.getRightTrigger(true);
        dashboard.displayPrintf(4, "leftTriggerPower = %f", leftTriggerPower);
        dashboard.displayPrintf(5, "rightTriggerPower = %f", rightTriggerPower);

        if (leftTriggerPower != 0.0 && rightTriggerPower != 0.0)
        {
            chainsaw.setPower(0.0);
        }
        else
        {
            chainsaw.setPower(-leftTriggerPower + rightTriggerPower);
        }
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
                    if (pressed)
                    {
                        cattleGuardDeployed.toggleState();
                        if (cattleGuardDeployed.getState())
                        {
                            cattleGuard.extend();
                        }
                        else
                        {
                            cattleGuard.retract();
                        }
                    }
                    break;

                case FtcGamepad.GAMEPAD_B:
                    break;

                case FtcGamepad.GAMEPAD_X:
                    break;

                case FtcGamepad.GAMEPAD_Y:
                    break;

                case FtcGamepad.GAMEPAD_LBUMPER:
                    if (pressed)
                    {
                        leftArm.extend();
                    }
                    else
                    {
                        leftArm.retract();
                    }
                    break;

                case FtcGamepad.GAMEPAD_RBUMPER:
                    if (pressed)
                    {
                        rightArm.extend();
                    }
                    else
                    {
                        rightArm.retract();
                    }
                    break;
            }
        }
        else if (gamepad == operatorGamepad)
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
