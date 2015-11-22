package ftc3543;

import ftclib.FtcGamepad;
import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcBooleanState;
import trclib.TrcRobot;

public class FtcTeleOp extends FtcOpMode implements FtcGamepad.ButtonHandler
{
    private HalDashboard dashboard;
    private FtcRobot robot;
    private FtcGamepad driverGamepad;
    private FtcGamepad operatorGamepad;
    private TrcBooleanState cattleGuardDeployed;

    //
    // Implements FtcOpMode abstract methods.
    //

    @Override
    public void robotInit()
    {
        //
        // Initializing global objects.
        //
        dashboard = HalDashboard.getInstance();
        robot = new FtcRobot(TrcRobot.RunMode.TELEOP_MODE);
        //
        // Initializing Gamepads.
        //
        driverGamepad = new FtcGamepad("DriverGamepad", gamepad1, this);
        operatorGamepad = new FtcGamepad("OperatorGamepad", gamepad2, this);
        driverGamepad.setYInverted(true);
        operatorGamepad.setYInverted(true);
        //
        // CattleGuard subsystem.
        //
        cattleGuardDeployed = new TrcBooleanState("cattleGuardDeployed", false);

    }   //robotInit

    @Override
    public void startMode()
    {
        //
        // There is an issue with the gamepad objects that may not be valid
        // before waitForStart() is called. So we call the setGamepad method
        // here to update their references in case they have changed.
        //
        driverGamepad.setGamepad(gamepad1);
        operatorGamepad.setGamepad(gamepad2);
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
        robot.driveBase.tankDrive(leftPower, rightPower);
        dashboard.displayPrintf(1, "leftPower = %.3f", leftPower);
        dashboard.displayPrintf(2, "rightPower = %.3f", rightPower);
        //
        // Elevator subsystem.
        //
        double elevatorPower = operatorGamepad.getRightStickY(true);
        robot.elevator.setPower(elevatorPower);
        dashboard.displayPrintf(3, "elevatorPower = %.3f, height=%.1f",
                                elevatorPower, robot.elevator.getHeight());
        dashboard.displayPrintf(4, "lowerLimit = %s, upperLimit = %s",
                                robot.elevator.isLowerLimitSwitchPressed()? "pressed": "released",
                                robot.elevator.isUpperLimitSwitchPressed()? "pressed": "released");
        robot.elevator.displayDebugInfo(5);
        //
        // SlideHook subsystem.
        //
        double slidePower = operatorGamepad.getLeftStickY(true);
        robot.slideHook.setPower(slidePower);
        dashboard.displayPrintf(7, "slidePower = %.3f, length=%.1f",
                                slidePower, robot.slideHook.getLength());
        dashboard.displayPrintf(8, "lowerLimit = %s, upperLimit = %s",
                                robot.slideHook.isLowerLimitSwitchPressed()? "pressed": "released",
                                robot.slideHook.isUpperLimitSwitchPressed()? "pressed": "released");
        robot.slideHook.displayDebugInfo(9);
    }   //runPeriodic

    @Override
    public void runContinuous()
    {
    }   //runContinuous

    //
    // Implements FtcGamepad.ButtonHandler interface.
    //

    @Override
    public void gamepadButtonEvent(FtcGamepad gamepad, final int btnMask, final boolean pressed)
    {
        dashboard.displayPrintf(15, "%s: %04x->%s",
                gamepad.toString(), btnMask, pressed? "Pressed": "Released");
        if (gamepad == driverGamepad)
        {
            switch (btnMask)
            {
                case FtcGamepad.GAMEPAD_A:
                    break;

                case FtcGamepad.GAMEPAD_B:
                    break;

                case FtcGamepad.GAMEPAD_Y:
                    break;

                case FtcGamepad.GAMEPAD_LBUMPER:
                    break;

                case FtcGamepad.GAMEPAD_RBUMPER:
                    break;
            }
        }
        else if (gamepad == operatorGamepad)
        {
            switch (btnMask)
            {
                case FtcGamepad.GAMEPAD_A:
                    if (pressed)
                    {
                        cattleGuardDeployed.toggleState();
                        if (cattleGuardDeployed.getState())
                        {
                            robot.cattleGuard.extend();
                        }
                        else
                        {
                            robot.cattleGuard.retract();
                        }
                    }
                    break;

                case FtcGamepad.GAMEPAD_B:
                    if (pressed)
                    {
                        robot.buttonPusher.pushRightButton();
                    }
                    else
                    {
                        robot.buttonPusher.retract();
                    }
                    break;

                case FtcGamepad.GAMEPAD_X:
                    if (pressed)
                    {
                        robot.buttonPusher.pushLeftButton();
                    }
                    else
                    {
                        robot.buttonPusher.retract();
                    }
                    break;

                case FtcGamepad.GAMEPAD_Y:
                    break;

                case FtcGamepad.GAMEPAD_LBUMPER:
                    if (pressed)
                    {
                        robot.leftWing.setPosition(RobotInfo.WING_LEFT_EXTEND_POSITION);
                    }
                    else
                    {
                        robot.leftWing.setPosition(RobotInfo.WING_LEFT_RETRACT_POSITION);
                    }
                    break;

                case FtcGamepad.GAMEPAD_RBUMPER:
                    if (pressed)
                    {
                        robot.rightWing.setPosition(RobotInfo.WING_RIGHT_EXTEND_POSITION);
                    }
                    else
                    {
                        robot.rightWing.setPosition(RobotInfo.WING_RIGHT_RETRACT_POSITION);
                    }
                    break;

                case FtcGamepad.GAMEPAD_START:
                    if (pressed)
                    {
                        robot.elevator.zeroCalibrate(RobotInfo.ELEVATOR_CAL_POWER);
                    }
                    break;

                case FtcGamepad.GAMEPAD_BACK:
                    if (pressed)
                    {
                        robot.slideHook.zeroCalibrate(RobotInfo.SLIDEHOOK_CAL_POWER);
                    }
                    break;

                case FtcGamepad.GAMEPAD_DPAD_UP:
                    if (pressed)
                    {
                        robot.hangingHook.extend();
                    }
                    break;

                case FtcGamepad.GAMEPAD_DPAD_DOWN:
                    if (pressed)
                    {
                        robot.hangingHook.retract();
                    }
                    break;

                case FtcGamepad.GAMEPAD_DPAD_LEFT:
                    if (pressed)
                    {
                        robot.elevator.setChainLock(false);
                    }
                    break;

                case FtcGamepad.GAMEPAD_DPAD_RIGHT:
                    if (pressed)
                    {
                        robot.elevator.setChainLock(true);
                    }
                    break;
            }
        }
    }   //gamepadButtonEvent

}   //class FtcTeleOp
