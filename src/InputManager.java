import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

/*	Taken from Brackeen's Textbook, deleted all mentions of Mouse, as it is not needed
 *   The InputManager manages input of key events.
 *   Events are mapped to GameActions.
 */

public class InputManager implements KeyListener
{
    // key codes are defined in java.awt.KeyEvent.
    // most of the codes (except for some rare ones like
    // "alt graph") are less than 600.
    private static final int NUM_KEY_CODES = 600;

    private GameAction[] keyActions =
        new GameAction[NUM_KEY_CODES];

    private Component comp;

    /**
        Creates a new InputManager that listens to input from the
        specified component.
    */
    public InputManager(Component comp) 
    {
        this.comp = comp;
        
        // register key listener
        this.comp.addKeyListener(this);

        // allow input of the TAB key and other keys normally
        // used for focus traversal
        this.comp.setFocusTraversalKeysEnabled(false);
    }

    /*  Maps a GameAction to a specific key. The key codes are
     *  defined in java.awt.KeyEvent. If the key already has
     *  a GameAction mapped to it, the new GameAction overwrites
     *  it.
     */
    public void MapToKey(GameAction gameAction, int keyCode) { keyActions[keyCode] = gameAction; }


    /**
        Clears all mapped keys and mouse actions to this
        GameAction.
    */
    public void ClearMap(GameAction gameAction) 
    {
        for (int i = 0; i < keyActions.length; i++) 
        {
            if (keyActions[i] == gameAction) { keyActions[i] = null; }
        }

        gameAction.Reset();
    }


    /**
        Gets a List of names of the keys and mouse actions mapped
        to this GameAction. Each entry in the List is a String.
    */
    public List<String> GetMaps(GameAction gameCode) 
    {
        ArrayList<String> list = new ArrayList<String>();

        for (int i = 0; i < keyActions.length; i++) 
        {
            if (keyActions[i] == gameCode) {list.add(GetKeyName(i));}
        }

        return list;
    }


    /**
        Resets all GameActions so they appear like they haven't
        been pressed.
    */
    public void ResetGameActions() 
    {
        for (int i = 0; i < keyActions.length; i++) 
        {
            if (keyActions[i] != null) 	 { keyActions[i].Reset(); }
        }

    }


    /**
        Gets the name of a key code.
    */
    public static String GetKeyName(int keyCode) { return KeyEvent.getKeyText(keyCode);}



    private GameAction GetKeyAction(KeyEvent e) 
    {
        int keyCode = e.getKeyCode();
        if (keyCode < keyActions.length) { return keyActions[keyCode]; }
        else { return null; }
    }



    // from the KeyListener interface
    public void keyPressed(KeyEvent e) {
        GameAction gameAction = GetKeyAction(e);
        if (gameAction != null) { gameAction.Press(); }
        // make sure the key isn't processed for anything else
        e.consume();
    }


    // from the KeyListener interface
    public void keyReleased(KeyEvent e) 
    {
        GameAction gameAction = GetKeyAction(e);
        if (gameAction != null) { gameAction.Release(); }
        // make sure the key isn't processed for anything else
        e.consume();
    }


    // from the KeyListener interface
    public void keyTyped(KeyEvent e) {
        // make sure the key isn't processed for anything else
        e.consume();
    }
}

