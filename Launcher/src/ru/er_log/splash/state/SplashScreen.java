package ru.er_log.splash.state;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import ru.er_log.Settings;
import ru.er_log.components.Frame;

public class SplashScreen {

    private static SplashFrame sframe;

    public static void start()
    {
        if (Settings.use_splash_screen[0]) createSplash();
        beforeStart();
        createMainFrame();
    }
    
    private static void createSplash()
    {
        Frame.report("Подготовка к запуску программы...");
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                sframe = new SplashFrame();
                sframe.setVisible(true);
            }
        });
    }
    
    private static void beforeStart()
    {
        status("подготовка к запуску...");
        
        Frame.beforeStart(sframe);
        
        status("запуск программы...");
    }
    
    private static void createMainFrame()
    {
//        SwingUtilities.invokeLater(new Runnable()
//        {
//            public void run() {
                Frame.start(sframe);
//            }
//        });
    }
    
    private static void status(final String status)
    {
        try
        {
            SwingUtilities.invokeAndWait(new Runnable()
            { public void run() { sframe.setStatus(status); } });
        } catch (InterruptedException | InvocationTargetException e) {}
    }
}