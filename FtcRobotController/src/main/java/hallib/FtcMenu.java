package hallib;

import java.util.ArrayList;

import trclib.TrcDbgTrace;

public class FtcMenu
{
    private static final String moduleName = "FtcMenu";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private static final long LOOP_INTERVAL = 200;

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

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
        }

        while (true)
        {
            if (menuButtons.isMenuUp())
            {
                prevChoice();
            }
            else if (menuButtons.isMenuDown())
            {
                nextChoice();
            }
            else if (menuButtons.isMenuOk())
            {
                break;
            }
            else if (menuButtons.isMenuCancel())
            {
                selectedChoice = -1;
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

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                    "=%d", selectedChoice);
        }

        return selectedChoice;
    }   //getChoice

    public double getChoiceValue(int choice)
    {
        return choiceValueTable.get(choice).doubleValue();
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

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC);
        }

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
        final String funcName = "prevChoice";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC);
        }

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
