/*
 * Titan Robotics Framework Library
 * Copyright (c) 2015 Titan Robotics Club (http://www.titanrobotics.net)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

    private static int prevButtonStates = 0;
    private static FtcMenu currMenu = null;

    private HalDashboard dashboard;
    private String menuTitle;
    private FtcMenu parent;
    private MenuButtons menuButtons;
    private ArrayList<String> choiceTextTable = new ArrayList<String>();
    private ArrayList<Object> choiceObjectTable = new ArrayList<Object>();
    private ArrayList<FtcMenu> childMenuTable = new ArrayList<FtcMenu>();
    private int currChoice = -1;
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
     * @param menuTitle specifies the title of the menu. The title will be displayed
     *                  as the first line in the menu.
     * @param parent specifies the parent menu to go back to if the BACK button
     *               is pressed. If this is the root menu, it can be set to null.
     * @param menuButtons specifies the object that implements the MenuButtons interface.
     */
    public FtcMenu(String menuTitle, FtcMenu parent, MenuButtons menuButtons)
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
        this.parent = parent;
        this.menuButtons = menuButtons;
    }   //FtcMenu

    /**
     * This method adds a choice to the menu. The choices will be displayed in the
     * order of them being added.
     *
     * @param choiceText specifies the choice text that will be displayed on the dashboard.
     * @param choiceObj specifies the object to be returned if the choice is selected.
     * @param childMenu specifies the next menu to go to when this choice is selected.
     *                  If this is the last menu (a leaf node in the tree), it can be set
     *                  to null.
     */
    public void addChoice(String choiceText, Object choiceObj, FtcMenu childMenu)
    {
        final String funcName = "addChoice";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "text=%s,obj=%s,child=%s",
                    choiceText, choiceObj.toString(),
                    childMenu == null? "null": childMenu.getTitle());
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        choiceTextTable.add(choiceText);
        choiceObjectTable.add(choiceObj);
        childMenuTable.add(childMenu);
        if (currChoice == -1)
        {
            //
            // This is the first added choice in the menu.
            // Make it the default choice by highlighting it.
            //
            currChoice = 0;
        }
    }   //addChoice

    /**
     * This method adds a choice to the menu. The choices will be displayed in the
     * order of them being added.
     *
     * @param choiceText specifies the choice text that will be displayed on the dashboard.
     * @param choiceObj specifies the object to be returned if the choice is selected.
     */
    public void addChoice(String choiceText, Object choiceObj)
    {
        addChoice(choiceText, choiceObj, null);
    }   //addChoice

    /**
     * This method returns the parent menu of this menu.
     *
     * @return parent menu (can be null if this menu is the root menu).
     */
    public FtcMenu getParentMenu()
    {
        final String funcName = "getParentMenu";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%s", parent.getTitle());
        }

        return parent;
    }   //getParentMenu

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
     * This method returns the choice text of the given choice index.
     *
     * @param choice specifies the choice index in the menu.
     * @return text of the choice if choice index is valid, null otherwise.
     */
    public String getChoiceText(int choice)
    {
        final String funcName = "getChoiceText";
        String text = null;
        int tableSize = choiceTextTable.size();

        if (tableSize > 0 && choice >= 0 && choice < tableSize)
        {
            text = choiceTextTable.get(choice);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "choice=%d", choice);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", text != null? text: "null");
        }

        return text;
    }   //getChoiceText

    /**
     * This method returns the choice object of the given choice index.
     *
     * @param choice specifies the choice index in the menu.
     * @return object of the given choice if choice index is valid, null otherwise.
     */
    public Object getChoiceObject(int choice)
    {
        final String funcName = "getChoiceObject";
        Object obj = null;
        int tableSize = choiceObjectTable.size();

        if (tableSize > 0 && choice >= 0 && choice < tableSize)
        {
            obj = choiceObjectTable.get(choice);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "choice=%d", choice);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", obj != null? obj.toString(): "null");
        }

        return obj;
    }   //getChoiceObject

    /**
     * This method returns the index of the current choice. Every menu has a
     * current choice even if the menu hasn't been displayed and the user
     * hasn't picked a choice. In that case, the current choice is the
     * highlighted selection of the menu which is the first choice in the menu.
     * If the menu is empty, the current choice index is -1.
     *
     * @return current choice index, -1 if menu is empty.
     */
    public int getCurrentChoice()
    {
        final String funcName = "getCurrentChoice";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%d", currChoice);
        }

        return currChoice;
    }   //getCurrentChoice

    /**
     * This method returns the text of the current choice. Every menu has a
     * current choice even if the menu hasn't been displayed and the user
     * hasn't picked a choice. In that case, the current choice is the
     * highlighted selection of the menu which is the first choice in the menu.
     * If the menu is empty, the current choice index is -1.
     *
     * @return current choice text, null if menu is empty.
     */
    public String getCurrentChoiceText()
    {
        return getChoiceText(currChoice);
    }   //getCurrentChoiceText

    /**
     * This method returns the object of the current choice. Every menu has a
     * current choice even if the menu hasn't been displayed and the user
     * hasn't picked a choice. In that case, the current choice is the
     * highlighted selection of the menu which is the first choice in the menu.
     * If the menu is empty, the current choice index is -1.
     *
     * @return current choice object, null if menu is empty.
     */
    public Object getCurrentChoiceObject()
    {
        return getChoiceObject(currChoice);
    }   //getCurrentChoiceObject

    /**
     * This method sets the current menu to the specified root menu. Typically,
     * this is called in conjunction with the runMenus() method to use the FtcMenu
     * module in a non-blocking environment.
     *
     * @param rootMenu specifies the root menu.
     */
    public static void setRootMenu(FtcMenu rootMenu)
    {
        currMenu = rootMenu;
    }   //setRootMenu

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
        setRootMenu(rootMenu);
        while (!runMenus())
        {
            HalUtil.sleep(LOOP_INTERVAL);
            /*
            try
            {
                //
                // Make sure we yield so events get processed.
                //
                FtcOpMode.getInstance().waitOneFullHardwareCycle();
            }
            catch (InterruptedException e)
            {
            }
            */
        }
    }   //walkMenuTree

    /**
     * This method walks the menu tree in a non-blocking environment.
     * It means this method must be called periodically, so that the
     * caller can perform other tasks if necessary.
     *
     * @return true if the user finished selecting the last choice of
     *         the menu tree, false if the caller must call this again
     *         in a loop.
     */
    public static boolean runMenus()
    {
        boolean done = false;

        if (currMenu == null)
        {
            done = true;
        }
        else
        {
            int currButtonStates = currMenu.getMenuButtons();
            int changedButtons = currButtonStates ^ prevButtonStates;
            //
            // Refresh the display to show the choice movement.
            //
            currMenu.displayMenu();
            //
            // Check if any menu buttons changed states.
            //
            if (changedButtons != 0)
            {
                int buttonsPressed = currButtonStates & changedButtons;

                if ((buttonsPressed & MENUBUTTON_BACK) != 0)
                {
                    //
                    // MenuBack is pressed, goto parent menu unless it's already root menu.
                    // If at root menu, stay on it.
                    //
                    FtcMenu parentMenu = currMenu.getParentMenu();
                    if (parentMenu != null)
                    {
                        currMenu = parentMenu;
                    }
                }
                else if ((buttonsPressed & MENUBUTTON_ENTER) != 0)
                {
                    //
                    // MenuEnter is pressed, goto the child menu of the current choice.
                    // If there is none, we are done.
                    //
                    currMenu = currMenu.childMenuTable.get(currMenu.getCurrentChoice());
                    if (currMenu == null)
                    {
                        //
                        // We are done with the menus. Let's clear the dashboard.
                        //
                        HalDashboard.getInstance().clearDisplay();
                    }
                }
                else if ((buttonsPressed & MENUBUTTON_UP) != 0)
                {
                    //
                    // MenuUp is pressed. Move the selected choice up one.
                    //
                    currMenu.prevChoice();
                }
                else if ((buttonsPressed & MENUBUTTON_DOWN) != 0)
                {
                    //
                    // MenuDown is pressed. Move the selected choice down one.
                    //
                    currMenu.nextChoice();
                }

                prevButtonStates = currButtonStates;
            }
        }

        return done;
    }   //runMenus

    /**
     * This method checks all the menu button states and combine them into an integer,
     * one bit for each button.
     *
     * @return an integer representing the states of all the menu buttons.
     */
    private int getMenuButtons()
    {
        final String funcName = "getMenuButtons";

        int buttons = 0;

        if (menuButtons.isMenuBackButton()) buttons |= MENUBUTTON_BACK;
        if (menuButtons.isMenuEnterButton()) buttons |= MENUBUTTON_ENTER;
        if (menuButtons.isMenuUpButton()) buttons |= MENUBUTTON_UP;
        if (menuButtons.isMenuDownButton()) buttons |= MENUBUTTON_DOWN;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC, "=%x", buttons);
        }

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
                    i == currChoice? ">>\t%s": "%s", choiceTextTable.get(i));
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
            currChoice = -1;
        }
        else
        {
            currChoice++;
            if (currChoice >= choiceTextTable.size())
            {
                currChoice = 0;
            }

            int lastDisplayedChoice =
                    Math.min(firstDisplayedChoice + HalDashboard.MAX_NUM_TEXTLINES - 2,
                             choiceTextTable.size() - 1);
            if (currChoice > lastDisplayedChoice)
            {
                //
                // Scroll down.
                //
                firstDisplayedChoice = currChoice - (HalDashboard.MAX_NUM_TEXTLINES - 2);
            }
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC,
                               "! (choice=%d)", currChoice);
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
            currChoice = -1;
        }
        else
        {
            currChoice--;
            if (currChoice < 0)
            {
                currChoice = choiceTextTable.size() - 1;
            }

            if (currChoice < firstDisplayedChoice)
            {
                //
                // Scroll up.
                //
                firstDisplayedChoice = currChoice;
            }
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC,
                               "! (choice=%d)", currChoice);
        }
    }   //prevChoice

}   //class FtcMenu
