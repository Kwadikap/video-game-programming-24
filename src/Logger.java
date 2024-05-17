/*
 * This class is used as a util for debugging purposes
 * Makes it easier to print something to the screen 
 * without having to type system.out.print everytime
 */
public class Logger {
    public static void log(String msg) {
        System.out.println(msg);
    }

    public static void log(Object obj) {
        System.out.println(obj);
    }
}
