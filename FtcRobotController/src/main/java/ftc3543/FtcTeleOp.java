package ftc3543;

import ftclib.FtcGamepad;
import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcRobot;

public class FtcTeleOp extends FtcOpMode implements FtcGamepad.ButtonHandler
{
    protected HalDashboard dashboard;
    protected Robot robot;
    private FtcGamepad driverGamepad;
    private FtcGamepad operatorGamepad;

    private boolean invertedDrive = false;
    //
    // Implements FtcOpMode abstract method.
    //

    @Override
    public void initRobot()
    {
        //
        // Initializing global objects.
        //
        dashboard = HalDashboard.getInstance();
        robot = new Robot(TrcRobot.RunMode.TELEOP_MODE);
        //
        // Initializing Gamepads.
        //
        driverGamepad = new FtcGamepad("DriverGamepad", gamepad1, this);
        operatorGamepad = new FtcGamepad("OperatorGamepad", gamepad2, this);
        driverGamepad.setYInverted(true);
        operatorGamepad.setYInverted(true);
    }   //initRobot

    //
    // Overrides TrcRobot.RobotMode methods.
    //

    @Override
    public void startMode()
    {
        dashboard.clearDisplay();
        robot.startMode(TrcRobot.RunMode.TELEOP_MODE);
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
        robot.stopMode(TrcRobot.RunMode.TELEOP_MODE);
    }   //stopMode

    @Override
    public void runPeriodic(double elapsedTime)
    {
        //
        // DriveBase subsystem.
        //
        double leftPower  = driverGamepad.getLeftStickY(true);
        double rightPower = driverGamepad.getRightStickY(true);
        robot.driveBase.tankDrive(leftPower, rightPower, invertedDrive);
        dashboard.displayPrintf(1, "leftPower=%.2f,rightPower=%.2f", leftPower, rightPower);
        dashboard.displayPrintf(2, "yPos=%.2f,heading=%.2f",
                                robot.driveBase.getYPosition(), robot.driveBase.getHeading());
        //
        // Winch subsystem.
        //
        double winchPower = operatorGamepad.getRightStickY(true);
        robot.winch.setPower(winchPower);
        dashboard.displayPrintf(3, "Winch:power=%.2f,len=%.2f,limitSW=%d",
                                winchPower, robot.winch.getLength(),
                                robot.winch.isLowerLimitSwitchPressed()? 1: 0);
        //
        // Winch Tilter.
        //
        double tilterPower = operatorGamepad.getLeftStickY(true);
        robot.winch.setTilterPower(tilterPower);
        dashboard.displayPrintf(4, "Tilter:power=%.2f,pos=%.2f",
                                tilterPower, robot.winch.getTilterPosition());
    }   //runPeriodic

    //
    // Implements FtcGamepad.ButtonHandler interface.
    //

    @Override
    public void gamepadButtonEvent(FtcGamepad gamepad, int button, boolean pressed)
    {
        dashboard.displayPrintf(7, "%s: %04x->%s",
                gamepad.toString(), button, pressed? "Pressed": "Released");
        if (gamepad == driverGamepad)
        {
            switch (button)
            {
                case FtcGamepad.GAMEPAD_A:
                    break;

                case FtcGamepad.GAMEPAD_B:
                    break;

                case FtcGamepad.GAMEPAD_X:
                    break;

                case FtcGamepad.GAMEPAD_Y:
                    break;

                case FtcGamepad.GAMEPAD_RBUMPER:
                    invertedDrive = pressed;
                    break;
            }
        }
        else if (gamepad == operatorGamepad)
        {
            switch (button)
            {
                case FtcGamepad.GAMEPAD_A:
                    if (pressed)
                    {
                        robot.winch.setBrakeOn(false);
                    }
                    break;

                case FtcGamepad.GAMEPAD_Y:
                    if (pressed)
                    {
                        robot.winch.setBrakeOn(true);
                    }
                    break;

                case FtcGamepad.GAMEPAD_X:
                    if (pressed)
                    {
                        robot.leftButtonPusher.extend();
                    }
                    else
                    {
                        robot.leftButtonPusher.retract();
                    }
                    break;

                case FtcGamepad.GAMEPAD_B:
                    if (pressed)
                    {
                        robot.rightButtonPusher.extend();
                    }
                    else
                    {
                        robot.rightButtonPusher.retract();
                    }
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
                        robot.winch.zeroCalibrate();
                    }
                    break;

                case FtcGamepad.GAMEPAD_DPAD_UP:
                    if (pressed)
                    {
                        robot.climberDepositor.setPosition(RobotInfo.DEPOSITOR_EXTEND_POSITION);
                    }
                    break;

                case FtcGamepad.GAMEPAD_DPAD_DOWN:
                    if (pressed)
                    {
                        robot.climberDepositor.setPosition(RobotInfo.DEPOSITOR_RETRACT_POSITION);
                    }
                    break;

                case FtcGamepad.GAMEPAD_DPAD_LEFT:
                    if (pressed)
                    {
                        robot.climberDepositor.setPosition(RobotInfo.DEPOSITOR_MIN_POSITION);
                    }
                    break;

                case FtcGamepad.GAMEPAD_DPAD_RIGHT:
                    break;
            }
        }
    }   //gamepadButtonEvent

}   //class FtcTeleOp
