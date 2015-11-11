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
    private TrcEnhancedServo cattleGuardServo;

    public CattleGuard()
    {
        leftServo = new FtcServo("leftCattleGuard");
        rightServo = new FtcServo("rightCattleGuard");
        rightServo.setReverse(true);
        cattleGuardServo = new TrcEnhancedServo(leftServo, rightServo);
    }   //CattleGuard

    public void extend()
    {
        cattleGuardServo.setPosition(RobotInfo.CATTLEGUARD_EXTEND_POSITION);
    }   //extend

    public void retract()
    {
        cattleGuardServo.setPosition(RobotInfo.CATTLEGUARD_RETRACT_POSITION);
    }   //retract

}   //class CattleGuard
