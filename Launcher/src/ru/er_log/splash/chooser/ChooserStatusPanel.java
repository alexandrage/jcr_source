package ru.er_log.splash.chooser;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;
import ru.er_log.Settings;
import ru.er_log.Style;
import ru.er_log.components.Components;
import ru.er_log.components.ThemeElements;
import ru.er_log.utils.BaseUtils;
import ru.er_log.utils.StyleUtils;

public class ChooserStatusPanel extends JPanel {
    
    private static final BufferedImage statusBack = BaseUtils.openLocalImage(BaseUtils.getTheme().themeChooserStatusBack());
    
    private final Components compt;
    
    private String tmpString = "";
    private boolean drawIcon = false;
    private int iconCut;
    
    private Timer timer_wait;
    private int waitIconState;
    private boolean drawWaitIcon;
    private Timer timer_pane;
    private int pane_Y_coord;
    
    public ChooserStatusPanel()
    {
        compt = new Components();
        this.setOpaque(false);
        this.setLayout(null);
        this.setFocusable(false);
        this.setSize(statusBack.getWidth(), statusBack.getHeight());
        this.setBounds(0, Settings.chooser_height[0] - getHeight(), getWidth(), getHeight());
    }
    
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(statusBack, 0, 0, getWidth(), getHeight(), null);
        
        g2d.setColor(Color.decode(Style.chooser_status_text_color));
        g2d.setFont(StyleUtils.getFont(Style.chooser_status_font_size, Style.chooser_status_font_num));
        
        drawCenteredString(tmpString, getWidth(), Style.chooser_status_Y, Color.BLUE, g2d);
        if (drawIcon) compt.drawImage(ThemeElements.alertIcons.getSubimage(iconCut, 0, 13, 13), ((getWidth() - g2d.getFontMetrics().stringWidth(tmpString)) / 2) - 19, Style.chooser_status_Y - 12, Color.PINK, g2d);
        if (drawWaitIcon) compt.drawImage(ThemeElements.waitIcon.getSubimage(waitIconState, 0, 36, 12), 122, 11, Color.PINK, g2d);
    }
    
    public void openPane()
    {
        timer_pane = new Timer(50, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (pane_Y_coord != getHeight())
                {
                    pane_Y_coord += 1;
                    setBounds(0, Settings.chooser_height[0] - getHeight() + pane_Y_coord, getWidth(), getHeight());
                    repaint();
                } else
                {
                    timer_pane.stop();
                }
            }
        }); timer_pane.start();
    }
    
    public void setStatus(String state, int type)
    {
        waitIcon(false);
        tmpString = state;
        drawIcon = true;
        if (type == 0) drawIcon = false;
        else if (type == 1) iconCut = 0;
        else if (type == 2) iconCut = 13;
        else if (type == 3) iconCut = 26;
        repaint();
    }
    
    public void waitIcon(boolean working)
    {
        if (timer_wait != null) timer_wait.stop(); timer_wait = null;
        tmpString = ""; drawIcon = false;
        
        if (working)
        {
            drawWaitIcon = true;
            timer_wait = new Timer(450, new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if (waitIconState == 72) waitIconState = 0;
                    else if (waitIconState == 36) waitIconState = 72;
                    else if (waitIconState == 0) waitIconState = 36;
                    repaint();
                }
            }); timer_wait.start();
        } else
        {
            drawWaitIcon = false;
            repaint();
        }
    }
    
    public static void drawCenteredString(String s, int width, int y, Color color, Graphics2D g2d)
    {
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(s)) / 2;

        g2d.drawString(s, x, y);
        if (Settings.draw_borders[0])
        {
            g2d.setColor(color);
            g2d.drawRect(x, (int) (y - fm.getHeight() / 1.35F), fm.stringWidth(s) - 1, fm.getHeight() - 1);
        }
    }
}
