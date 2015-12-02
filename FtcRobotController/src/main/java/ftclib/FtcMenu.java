package ftclib;

import java.util.ArrayList;

import hallib.HalDashboard;
import hallib.HalUtil;
import trclib.TrcDbgTrace;

/**
 * This class implements a display menu system. It allows you to construct a
 * menu tree structure where a menu is displayed on the Driver Station using
 * the Dashboard class. The user can press the up/down buttons to change the
 * highlighted choice on the menu and then press enter to select the highlighted
 * choice. After the choice is made, it will move on to the next menu in the
 * menu tree. Or if the user presses the back button to cancel the menu, it
 * will go back to the previous menu in the menu tree.
 * This is very useful in autonomous allowing the user to select from different
 * autonomous strategies and also select the options for each autonomous
 * strategy. For example, one could have a menu to select between being in the
 * RED alliance or the BLUE alliance. A menu to select the robot starting
 * position. A menu to select the autonomous strategy. A menu to select the
 * delay for starting the strategy etc.
 */
public class FtcMenu
{
    private static final String moduleName = "FtcMenu";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private static final long LOOP_INTERVAL     = 50;

    private static final int MENUBUTTON_BACK    = (1 << 0);
    private static final int MENUBUTTON_ENTER   = (1 << 1);
    private static final int MENUBUTTON_UP      = (1 << 2);
    private static final int MENUBUTTON_DOWN    = (1 << 3);

    private static int prevMenuButtons = 0;

    private HalDashboard dashboard;
    private FtcMenu parent;
    private String menuTitle;
    private MenuButtons menuButtons;
    private ArrayList<String> choiceTextTable = new ArrayList<String>();
    private ArrayList<Double> choiceValueTable = new ArrayList<Double>();
    private ArrayList<FtcMenu> childMenuTable = new ArrayList<FtcMenu>();
    private int selectedChoice = -1;
    private int firstDisplayedChoice = 0;

    /**
     * The user of this class is required to implement the MenuButtons
     * interface. The methods in this interface allows this class to
     * check for button activities the user made without hard coding
     * what particular buttons are associated with up/down/enter/back.
     * So you can associate the activities with gamepad buttons or even
     * other input devices.
     */
    public interface MenuButtons
    {
        /**
         * This method is called by this class to check if the UP button
         * is pressed.
         *
         * @return true if the UP button is pressed, false otherwise.
         */
        public boolean isMenuUpButton();

        /**
         * This method is called by this class to check if the DOWN button
         * is pressed.
         *
         * @return true if the DOWN button is pressed, false otherwise.
         */
        public boolean isMenuDownButton();

        /**
         * This method is called by this class to check if the ENTER button
         * is pressed.
         *
         * @return true if the ENTER button is pressed, false otherwise.
         */
        public boolean isMenuEnterButton();

        /**
         * This method is called by this class to check if the BACK button
         * is pressed.
         *
         * @return true if the BACK button is pressed, false otherwise.
         */
        public boolean isMenuBackButton();
    }   //interface MenuButtons

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param parent specifies the parent menu to go back to if the BACK button
     *               is pressed. If this is the root menu, it can be set to null.
     * @param menuTitle specifies the title of the menu. The title will be displayed
     *                  as the first line in the menu.
     * @param menuButtons specifies the object that implements the MenuButtons interface.
     */
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

    /**
     * This method adds a choice to the menu. The choices will be displayed in the
     * order of them being added.
     *
     * @param choiceText specifies the choice text that will be displayed on the dashboard.
     * @param choiceValue specifies the value to be returned if the choice is selected.
     * @param childMenu specifies the next menu to go to when this choice is selected.
     *                  If this is the last menu (a leaf node in the tree), it can be set
     *                  to null.
     */
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
            //
            // This is the first added choice in the menu.
            // Make it the default choice by highlighting it.
            //
            selectedChoice = 0;
        }
    }   //addChoice

    /**
     * This method adds a choice to the menu. The choices will be displayed in the
     * order of them being added.
     *
     * @param choiceText specifies the choice text that will be displayed on the dashboard.
     * @param choiceValue specifies the value to be returned if the choice is selected.
     */
    public void addChoice(String choiceText, double choiceValue)
    {
        addChoice(choiceText, choiceValue, null);
    }   //addChoice

    /**
     * This method traverses the menu tree from the given root menu displaying each
     * menu and waiting for the user to select a choice. When the user makes a choice,
     * it will go to the next menu from that choice. If the user cancels the menu, it
     * will go back to the parent menu where it came from. When the user makes a choice
     * and there is no next menu from that choice, the traversal is ended.
     * Note: this is a static method, meaning you can call it without a menu instance.
     *
     * @param rootMenu specifies the root of the menu tree.
     */
    public static void walkMenuTree(FtcMenu rootMenu)
    {
        FtcMenu menu = rootMenu;

        while (menu != null)
        {
            int choice = menu.getChoice();
            if (choice != -1)
            {
                //
                // User selected a choice, let's go to the next menu.
                //
                menu = menu.childMenuTable.get(choice);
            }
            else if (menu != rootMenu)
            {
                //
                // User canceled a menu, let's go back to the parent menu
                // unless we are already at the root menu in which case
                // we stay in the root menu.
                //
                menu = menu.getParent();
            }
        }
        //
        // We are done with the menus. Let's clear the dashboard.
        //
        HalDashboard.getInstance().clearDisplay();
    }   //walkMenuTree

    /**
     * This method returns the parent menu of this menu.
     *
     * @return parent menu (can be null if this menu is the root menu).
     */
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

    /**
     * This method returns the title text of this menu.
     *
     * @return title text.
     */
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

    /**
     * This method displays the menu and waits for the user to navigate the selections
     * and make a choice.
     * Note: this is a blocking method, it won't return until a choice is made or the
     * menu is canceled.
     *
     * @return choice index of the selection, -1 if the menu is canceled.
     */
    public int getChoice()
    {
        final String funcName = "getChoice";
        int choice = -1;
        boolean done = false;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
        }

        while (!done)
        {
            int currMenuButtons = getMenuButtons();
            int changedButtons = currMenuButtons ^ prevMenuButtons;
            //
            // Check if any menu buttons changed states.
            //
            if (changedButtons != 0)
            {
                int buttonsPressed = currMenuButtons & changedButtons;

                if ((buttonsPressed & MENUBUTTON_BACK) != 0)
                {
                    //
                    // MenuCancel is pressed. Set choice to none and exit.
                    //
                    choice = -1;
                    done = true;
                }
                else if ((buttonsPressed & MENUBUTTON_ENTER) != 0)
                {
                    //
                    // MenuEnter is pressed. Set choice to the selected choice and exit.
                    //
                    choice = selectedChoice;
                    done = true;
                }
                else if ((buttonsPressed & MENUBUTTON_UP) != 0)
                {
                    //
                    // MenuUp is pressed. Move the selected choice up one.
                    //
                    prevChoice();
                }
                else if ((buttonsPressed & MENUBUTTON_DOWN)!= 0)
                {
                    //
                    // MenuDown is pressed. Move the selected choice down one.
                    //
                    nextChoice();
                }

                prevMenuButtons = currMenuButtons;
            }
            //
            // Refresh the display to show the choice movement.
            //
            displayMenu();
            HalUtil.sleep(LOOP_INTERVAL);
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                    "=%d", choice);
        }

        return choice;
    }   //getChoice

    /**
     * This method returns the value of the selected choice. If the menu is canceled.
     * -1.0 is returned.
     * Note: this is a blocking method, it won't return until a choice is made or
     * canceled.
     *
     * @return selected choice value, -1.0 if cenceled.
     */
    public double getChoiceValue()
    {
        double value = -1.0;

        if (getChoice() != -1)
        {
            value = getSelectedChoiceValue();
        }

        return value;
    }   //getChoiceValue

    /**
     * This method returns the text of the selected choice. If the menu is canceled
     * null is returned.
     * Note: this is a blocking method, it won't return until a choice is made or
     * canceled.
     *
     * @return selected choice text, null if canceled.
     */
    public String getChoiceText()
    {
        String text = null;

        if (getChoice() != -1)
        {
            text = getSelectedChoiceText();
        }

        return text;
    }   //getChoiceText

    /**
     * This method returns the choice index of the current selection. Every menu
     * has a current selection even if the menu hasn't been displayed and the
     * user hasn't picked a choice. In that case, the current selection is the
     * default selection of the menu which is the first choice in the menu. If
     * the menu is empty, the current selection choice index is -1.
     *
     * @return selected choice index, -1 if menu is empty.
     */
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

    /**
     * This method returns the choice value of the current selection. Every menu
     * has a current selection even if the menu hasn't been displayed and the
     * user hasn't picked a choice. In that case, the current selection is the
     * default selection of the menu which is the first choice in the menu. If
     * the menu is empty, the current selection value is -1.
     *
     * @return selected choice text, -1 if there is no selection.
     */
    public double getSelectedChoiceValue()
    {
        final String funcName = "getSelectedChoiceValue";
        double value =
                selectedChoice == -1? -1.0: choiceValueTable.get(selectedChoice).doubleValue();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%f", value);
        }

        return value;
    }   //getSelectedChoiceValue

    /**
     * This method returns the choice text of the current selection. Every menu
     * has a current selection even if the menu hasn't been displayed and the
     * user hasn't picked a choice. In that case, the current selection is the
     * default selection of the menu which is the first choice in the menu. If
     * the menu is empty, the current selection text is null.
     *
     * @return selected choice text, null if there is no selection.
     */
    public String getSelectedChoiceText()
    {
        final String funcName = "getSelectedChoiceValue";
        String text =
                selectedChoice == -1? null: choiceTextTable.get(selectedChoice);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", text);
        }

        return text;
    }   //getSelectedChoiceText

    /**
     * This method returns the choice value of the given choice index.
     *
     * @param choice specifies the choice index in the menu.
     * @return value of the choice.
     */
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

    /**
     * This method returns the choice text of the given choice index.
     *
     * @param choice specifies the choice index in the menu.
     * @return text of the choice.
     */
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

    /**
     * This method checks all the menu button states and combine them into an integer,
     * one bit for each button.
     *
     * @return an integer representing the states of all the menu buttons.
     */
    private int getMenuButtons()
    {
        int buttons = 0;

        if (menuButtons.isMenuBackButton()) buttons |= MENUBUTTON_BACK;
        if (menuButtons.isMenuEnterButton()) buttons |= MENUBUTTON_ENTER;
        if (menuButtons.isMenuUpButton()) buttons |= MENUBUTTON_UP;
        if (menuButtons.isMenuDownButton()) buttons |= MENUBUTTON_DOWN;

        return buttons;
    }   //getMenuButtons

    /**
     * This method displays the menu on the dashboard with the current
     * selection highlighted. The number of choices in the menu may
     * exceed the total number of lines on the dashboard. In that case,
     * it will only display all the choices that will fit on the
     * dashboard. If the user navigates to a choice outside of the
     * dashboard display, the choices will scroll up or down to bring
     * the new selection into the dashboard.
     */
    private void displayMenu()
    {
        final String funcName = "displayMenu";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC);
        }

        //
        // Determine the choice of the last display line on the dashboard.
        //
        int lastDisplayedChoice =
                Math.min(firstDisplayedChoice + HalDashboard.MAX_NUM_TEXTLINES - 2,
                         choiceTextTable.size() - 1);
        dashboard.clearDisplay();
        dashboard.displayPrintf(0, menuTitle);
        //
        // Display all the choices that will fit on the dashboard.
        //
        for (int i = firstDisplayedChoice; i <= lastDisplayedChoice; i++)
        {
            dashboard.displayPrintf(
                    i - firstDisplayedChoice + 1,
                    i == selectedChoice? ">>\t%s": "%s",
                    choiceTextTable.get(i));
        }
    }   //displayMenu

    /**
     * This method moves the current selection to the next choice in the menu.
     * If it is already the last choice, it will wraparound back to the first choice.
     */
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
                //
                // Scroll down.
                //
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

    /**
     * This method moves the current selection to the previous choice in the menu.
     * If it is already the first choice, it will wraparound back to the last choice.
     */
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
                //
                // Scroll up.
                //
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
