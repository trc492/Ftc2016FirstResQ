package ftc3543.opmodes;

import ftclib.FtcServo;
import trclib.TrcEnhancedServo;

public class CattleGuard
{
    //
    // This component consists of two servo motors that
    // will deploy the cattle guard out to deflect debris.
    // It provides a method to deploy or undeploy the
    // cattle guard.
    //
    private FtcServo leftServo;
    private FtcServo rightServo;

    public CattleGuard()
    {
        leftServo = new FtcServo("leftCattleGuard");
        rightServo = new FtcServo("rightCattleGuard");
    }   //CattleGuard

    public void extend()
    {
        leftServo.setPosition(RobotInfo.CATTLEGUARD_LEFT_EXTEND_POSITION);
        rightServo.setPosition(RobotInfo.CATTLEGUARD_RIGHT_EXTEND_POSITION);
    }   //extend

    public void retract()
    {
        leftServo.setPosition(RobotInfo.CATTLEGUARD_LEFT_RETRACT_POSITION);
        rightServo.setPosition(RobotInfo.CATTLEGUARD_RIGHT_RETRACT_POSITION);
    }   //retract

}   //class CattleGuard
