package ru.er_log.splash.chooser;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import ru.er_log.Settings;
import ru.er_log.Style;
import ru.er_log.utils.BaseUtils;
import ru.er_log.utils.StyleUtils;

public class ChooserPanel extends JPanel {
    
    private static final BufferedImage chooserBack = BaseUtils.openLocalImage(BaseUtils.getTheme().themeChooserBack());
    
    public ChooserPanel()
    {
        this.setOpaque(false);
        this.setLayout(null);
        this.setFocusable(false);
        this.setSize(Settings.chooser_width[0], Settings.chooser_height[0]);
    }
    
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(chooserBack, 0, 0, Settings.chooser_width[0], Settings.chooser_height[0], null);
        
        g2d.setColor(Color.decode(Style.chooser_title_text_color));
        g2d.setFont(StyleUtils.getFont(Style.chooser_title_font_size, Style.chooser_title_font_num));
        
        drawCenteredString(Settings.title[0], getWidth(), Style.chooser_title_Y, Color.RED, g2d);
        
        Color c = Color.decode(Style.chooser_alert_text_color);
        g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), toValue(Style.chooser_alert_text_opacity)));
        g2d.setFont(StyleUtils.getFont(Style.chooser_alert_font_size, Style.chooser_alert_font_num));
        
        drawCenteredString("нажмите «Space» или «Enter» для продолжения", getWidth(), Style.chooser_alert_Y, Color.RED, g2d);
    }
    
    public static int toValue(int percent)
    {
        return (int) (255F / 100F * percent);
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
