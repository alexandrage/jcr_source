package ru.er_log.splash.chooser;

import javax.swing.SwingUtilities;
import ru.er_log.components.Frame;

public class ChooserScreen {
    
    public static void start()
    {
        createChooserFrame();
    }
    
    private static void createChooserFrame()
    {
        Frame.report("Вывод окна выбора сервера...");
        
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                ChooserFrame.cframe = new ChooserFrame();
                ChooserFrame.cframe.setVisible(true);
            }
        });
    }
}
