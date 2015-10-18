package hallib;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;

import trclib.TrcDbgTrace;

public class FtcMenu
{
    private static final String moduleName = "FtcMenu";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private static final long LOOP_INTERVAL = 300;

    private HalDashboard dashboard;
    private String menuTitle;
    private Gamepad gamepad;
    private ArrayList<String> choiceTextTable = new ArrayList<String>();
    private ArrayList<Double> choiceValueTable = new ArrayList<Double>();
    private int selectedChoice = -1;
    private int firstDisplayedChoice = 0;

    public FtcMenu(String menuTitle, Gamepad gamepad)
    {
        if (gamepad == null || menuTitle == null)
        {
            throw new NullPointerException("Gamepad/menuTitle must be provided");
        }
        dashboard = HalDashboard.getInstance();
        this.menuTitle = menuTitle;
        this.gamepad = gamepad;
    }   //FtcMenu

    public void addChoice(String choiceText, double choiceValue)
    {
        choiceTextTable.add(choiceText);
        choiceValueTable.add(choiceValue);
        if (selectedChoice == -1)
        {
            selectedChoice = 0;
        }
    }   //addChoice

    public double getChoice()
    {
        boolean aIsPressed = false;

        while (true)
        {
            if (gamepad.left_stick_y < -0.5)
            {
                prevChoice();
            }
            else if (gamepad.left_stick_y > 0.5)
            {
                nextChoice();
            }
            else if (!aIsPressed && gamepad.a)
            {
                aIsPressed = true;
                break;
            }
            displayMenu();

            try
            {
                Thread.sleep(LOOP_INTERVAL);
            }
            catch (InterruptedException e)
            {
            }
        }

        return selectedChoice == -1? 0.0:
                choiceValueTable.get(selectedChoice).doubleValue();
    }   //getChoice

    private void displayMenu()
    {
        int lastDisplayedChoice =
                Math.min(firstDisplayedChoice + HalDashboard.MAX_NUM_TEXTLINES - 2,
                         choiceTextTable.size() - 1);
        dashboard.clearDisplay();
        dashboard.displayPrintf(0, menuTitle);
        for (int i = firstDisplayedChoice; i <= lastDisplayedChoice; i++)
        {
            dashboard.displayPrintf(
                    i - firstDisplayedChoice + 1,
                    i == selectedChoice? ">>%s": "  %s",
                    choiceTextTable.get(i));
        }
    }   //displayMenu

    private void nextChoice()
    {
        if (choiceTextTable.size() == 0)
        {
            selectedChoice = -1;
        }
        else
        {
            selectedChoice++;
            if (selectedChoice >= choiceTextTable.size())
            {
                selectedChoice = 0;
            }
        }
    }   //nextChoice

    private void prevChoice()
    {
        if (choiceTextTable.size() == 0)
        {
            selectedChoice = -1;
        }
        else
        {
            selectedChoice--;
            if (selectedChoice < 0)
            {
                selectedChoice = choiceTextTable.size() - 1;
            }
        }
    }

}   //class FtcMenu
