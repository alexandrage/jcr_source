package ru.er_log.splash.state;

import com.sun.awt.AWTUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import ru.er_log.Settings;
import ru.er_log.utils.BaseUtils;

public class SplashFrame extends JFrame {

    private final SplashPanel panel = new SplashPanel();
    
    public SplashFrame()
    {
        this.setUndecorated(true);
        if (BaseUtils.getPlatform() != 0) AWTUtilities.setWindowOpaque(this, false);
        this.setPreferredSize(new Dimension(Settings.splash_width[0], Settings.splash_height[0]));
        this.setSize(this.getPreferredSize());
        this.setTitle(Settings.title[0] + " :: " + Settings.version[0]);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setIconImage(BaseUtils.openLocalImage(BaseUtils.getTheme().themeFavicon()));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        
        this.setContentPane(panel);
    }
    
    public void setStatus(final String status)
    {
        SplashPanel.status = "Состояние: " + status;
        panel.repaint();
    }
}