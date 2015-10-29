package hallib;

import java.util.ArrayList;

import trclib.TrcDbgTrace;

public class FtcMenu
{
    private static final String moduleName = "FtcMenu";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private static final long LOOP_INTERVAL = 50;

    private HalDashboard dashboard;
    private String menuTitle;
    private MenuButtons menuButtons;
    private ArrayList<String> choiceTextTable = new ArrayList<String>();
    private ArrayList<Double> choiceValueTable = new ArrayList<Double>();
    private int selectedChoice = -1;
    private int firstDisplayedChoice = 0;

    public interface MenuButtons
    {
        public boolean isMenuUp();
        public boolean isMenuDown();
        public boolean isMenuOk();
        public boolean isMenuCancel();
    }   //interface MenuButtons

    public FtcMenu(String menuTitle, MenuButtons menuButtons)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + menuTitle,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        if (menuButtons == null || menuTitle == null)
        {
            throw new NullPointerException("menuTitle/menuButtons must be provided");
        }

        dashboard = HalDashboard.getInstance();
        this.menuTitle = menuTitle;
        this.menuButtons = menuButtons;
    }   //FtcMenu

    public void addChoice(String choiceText, double choiceValue)
    {
        final String funcName = "addChoice";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "text=%s,value=%f", choiceText, choiceValue);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        choiceTextTable.add(choiceText);
        choiceValueTable.add(choiceValue);
        if (selectedChoice == -1)
        {
            selectedChoice = 0;
        }
    }   //addChoice

    public int getChoice()
    {
        final String funcName = "getChoice";
        boolean upButtonPressed = false;
        boolean downButtonPressed = false;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
        }

        while (true)
        {
            if (menuButtons.isMenuCancel())
            {
                selectedChoice = -1;
                break;
            }
            else if (menuButtons.isMenuOk())
            {
                break;
            }

            boolean isUp = menuButtons.isMenuUp();
            boolean isDown = menuButtons.isMenuDown();

            if (!upButtonPressed && isUp)
            {
                upButtonPressed = true;
                prevChoice();
            }
            else if (upButtonPressed && !isUp)
            {
                upButtonPressed = false;
            }

            if (!downButtonPressed && isDown)
            {
                downButtonPressed = true;
                nextChoice();
            }
            else if (downButtonPressed && !isDown)
            {
                downButtonPressed = false;
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

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                    "=%d", selectedChoice);
        }

        return selectedChoice;
    }   //getChoice

    public double getChoiceValue(int choice)
    {
        final String funcName = "getChoiceValue";
        double value = choiceValueTable.get(choice).doubleValue();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                               "choice=%d", choice);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%d", value);
        }

        return value;
    }   //getChoiceValue

    public double getChoiceValue()
    {
        return getChoiceValue(getChoice());
    }   //getChoiceValue

    private void displayMenu()
    {
        final String funcName = "displayMenu";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC);
        }

        int lastDisplayedChoice =
                Math.min(firstDisplayedChoice + HalDashboard.MAX_NUM_TEXTLINES - 2,
                         choiceTextTable.size() - 1);
        dashboard.clearDisplay();
        dashboard.displayPrintf(0, menuTitle);
        for (int i = firstDisplayedChoice; i <= lastDisplayedChoice; i++)
        {
            dashboard.displayPrintf(
                    i - firstDisplayedChoice + 1,
                    i == selectedChoice? ">>%s": "%s",
                    choiceTextTable.get(i));
        }
    }   //displayMenu

    private void nextChoice()
    {
        final String funcName = "nextChoice";

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

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC,
                               "! (choice=%d)", selectedChoice);
        }
    }   //nextChoice

    private void prevChoice()
    {
        final String funcName = "prevChoice";

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

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC,
                               "! (choice=%d)", selectedChoice);
        }
    }   //prevChoice

}   //class FtcMenu
