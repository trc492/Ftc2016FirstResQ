package ftclib;

import java.util.ArrayList;

import hallib.HalDashboard;
import trclib.TrcDbgTrace;

public class FtcMenu
{
    private static final String moduleName = "FtcMenu";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private static final long LOOP_INTERVAL = 50;

    private HalDashboard dashboard;
    private FtcMenu parent;
    private String menuTitle;
    private MenuButtons menuButtons;
    private ArrayList<String> choiceTextTable = new ArrayList<String>();
    private ArrayList<Double> choiceValueTable = new ArrayList<Double>();
    private ArrayList<FtcMenu> childMenuTable = new ArrayList<FtcMenu>();
    private int selectedChoice = -1;
    private int firstDisplayedChoice = 0;

    public interface MenuButtons
    {
        public boolean isMenuUp();
        public boolean isMenuDown();
        public boolean isMenuOk();
        public boolean isMenuCancel();
    }   //interface MenuButtons

    public FtcMenu(FtcMenu parent, String menuTitle, MenuButtons menuButtons)
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
        this.parent = parent;
        this.menuTitle = menuTitle;
        this.menuButtons = menuButtons;
    }   //FtcMenu

    public void addChoice(String choiceText, double choiceValue, FtcMenu childMenu)
    {
        final String funcName = "addChoice";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "text=%s,value=%f,child=%s",
                    choiceText, choiceValue, childMenu == null? "null": childMenu.getTitle());
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        choiceTextTable.add(choiceText);
        choiceValueTable.add(choiceValue);
        childMenuTable.add(childMenu);
        if (selectedChoice == -1)
        {
            selectedChoice = 0;
        }
    }   //addChoice

    public void addChoice(String choiceText, double choiceValue)
    {
        addChoice(choiceText, choiceValue, null);
    }   //addChoice

    public void walkMenuTree()
    {
        FtcMenu menu = this;

        while (menu != null)
        {
            int choice = menu.getChoice();
            if (choice != -1)
            {
                menu = childMenuTable.get(choice);
            }
            else if (menu != this)
            {
                menu = menu.getParent();
            }
        }
    }   //walkMenuTree

    public FtcMenu getParent()
    {
        final String funcName = "getParent";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%s", parent.getTitle());
        }

        return parent;
    }   //getParent

    public String getTitle()
    {
        final String funcName = "getTitle";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%s", menuTitle);
        }

        return menuTitle;
    }   //getTitle

    public int getChoice()
    {
        final String funcName = "getChoice";
        int choice = -1;
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
                choice = -1;
                break;
            }
            else if (menuButtons.isMenuOk())
            {
                choice = selectedChoice;
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
                    "=%d", choice);
        }

        return choice;
    }   //getChoice

    public double getChoiceValue()
    {
        double value = -1.0;

        if (getChoice() != -1)
        {
            value = getSelectedChoiceValue();
        }

        return value;
    }   //getChoiceValue

    public String getChoiceText()
    {
        String text = null;

        if (getChoice() != -1)
        {
            text = getSelectedChoiceText();
        }

        return text;
    }   //getChoiceText

    public int getSelectedChoice()
    {
        final String funcName = "getSelectedChoice";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%d", selectedChoice);
        }

        return selectedChoice;
    }   //getSelectedChoice

    public double getSelectedChoiceValue()
    {
        final String funcName = "getSelectedChoiceValue";
        double value = choiceValueTable.get(selectedChoice).doubleValue();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%f", value);
        }

        return value;
    }   //getSelectedChoiceValue

    public String getSelectedChoiceText()
    {
        final String funcName = "getSelectedChoiceValue";
        String text = choiceTextTable.get(selectedChoice);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", text);
        }

        return text;
    }   //getSelectedChoiceText

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

    public String getChoiceText(int choice)
    {
        final String funcName = "getChoiceText";
        String text = choiceTextTable.get(choice);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "choice=%d", choice);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", text);
        }

        return text;
    }   //getChoiceText

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
                    i == selectedChoice? ">>\t%s": "%s",
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

            int lastDisplayedChoice =
                    Math.min(firstDisplayedChoice + HalDashboard.MAX_NUM_TEXTLINES - 2,
                             choiceTextTable.size() - 1);
            if (selectedChoice > lastDisplayedChoice)
            {
                firstDisplayedChoice = selectedChoice - (HalDashboard.MAX_NUM_TEXTLINES - 2);
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

            if (selectedChoice < firstDisplayedChoice)
            {
                firstDisplayedChoice = selectedChoice;
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
