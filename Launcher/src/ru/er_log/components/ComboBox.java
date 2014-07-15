package ru.er_log.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import ru.er_log.Settings;
import ru.er_log.Style;
import ru.er_log.utils.BaseUtils;
import ru.er_log.utils.ImageUtils;
import ru.er_log.utils.StyleUtils;
import static ru.er_log.Style.*;

public class ComboBox extends JComponent implements MouseListener, MouseMotionListener {
    
    public String[] elements;
    
    public int axisY = 0;
    public boolean selectValue = false;
    public boolean error = false;
    
    private final int paneY = 6;
    private boolean entered = false;
    private boolean pressed = false;
    private int selected = 0;
    private int y = 0;
    
    public static final BufferedImage def_line = ThemeElements.combobox.getSubimage(combobox_img_def_X, combobox_img_def_Y, combobox_img_def_width, combobox_img_def_height);
    public static final BufferedImage rol_line = ThemeElements.combobox.getSubimage(combobox_img_rol_X, combobox_img_rol_Y, combobox_img_rol_width, combobox_img_rol_height);
    public static final BufferedImage pre_line = ThemeElements.combobox.getSubimage(combobox_img_pre_X, combobox_img_pre_Y, combobox_img_pre_width, combobox_img_pre_height);
    public static final BufferedImage loc_line = ThemeElements.combobox.getSubimage(combobox_img_loc_X, combobox_img_loc_Y, combobox_img_loc_width, combobox_img_loc_height);
    public static final BufferedImage def_pane = ThemeElements.combobox.getSubimage(combobox_img_pane_X, combobox_img_pane_Y, combobox_img_pane_width, combobox_img_pane_height);
    
    private static Frame frame = null;
    private static Component cpanel = null;
    
    public ComboBox(String[] elements, int x, int y, Component comp)
    {
        reset(elements, x, y, comp);
        
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    public final void reset(String[] elements, int x, int y, Component comp)
    {
        try { frame = (Frame) comp; } catch (Exception e) { cpanel = comp; }
        
        try
        {
            for (int i = 0; i < elements.length; i++)
                elements[i] = (Settings.show_client_version[0] ? BaseUtils.servers[i].split(" :: ")[3] + ": " : "") + elements[i];

            if (frame != null && !Frame.isOffline())
            {
                String[] new_elements = new String[elements.length + 1];
                String[] new_servers = new String[BaseUtils.servers.length + 1];
                new_elements[0] = new_servers[0] = "Сменить сервер для игры";

                for (int i = 1; i <= elements.length; i++)
                {
                    new_elements[i] = elements[i - 1];
                    new_servers[i] = BaseUtils.servers[i - 1];
                }

                elements = new_elements;
                BaseUtils.servers = new_servers;
            } else if (cpanel != null && Settings.show_server_is_not_selected[0] && BaseUtils.getPropertyString("server") == null)
            {
                String[] new_elements = new String[elements.length + 1];
                String[] new_servers = new String[BaseUtils.servers.length + 1];
                new_elements[0] = new_servers[0] = "Сервер не выбран";

                for (int i = 1; i <= elements.length; i++)
                {
                    new_elements[i] = elements[i - 1];
                    new_servers[i] = BaseUtils.servers[i - 1];
                }

                elements = new_elements;
                BaseUtils.servers = new_servers;
            }
        } catch (Exception e) {}
        
        this.elements = elements;
        this.axisY = y;
        
        if (frame != null && this.elements.length > 7)
        {
            int new_width = Settings.frame_width[0];
            int new_height = Settings.frame_height[0] + ((this.elements.length - 7) * 15);
            comp.setSize(new_width, new_height);
            Frame.report("Размеры окна программы были автоматически изменены: " + new_width + " x " + new_height);
        } else if (cpanel != null && this.elements.length > 1)
        {
            int new_width = Settings.chooser_width[0];
            int new_height = Settings.chooser_height[0] + ((this.elements.length - 1) * 15);
            if (cpanel.getHeight() > new_height) new_height = cpanel.getHeight();
            comp.setSize(new_width, new_height);
            Frame.report("Размеры окна программы были автоматически изменены: " + new_width + " x " + new_height);
        }
        
        setForeground(Color.decode(Style.elements_text_color));
        setFont(StyleUtils.getFont(12, 1));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBounds(x, y, 232, 19);
        
        error = false;
        if (elements.length == 2 && elements[1].equals("error"))
        {
            error = true;
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        
        if (pressed && !error)
        {
            g2d.drawImage(pre_line, 0, 0, w, pre_line.getHeight(), null);
            
            int righth = pre_line.getHeight() + (elements.length * 8) + (elements.length * 7 - 7) + (paneY * 3) - 2; // Высота выпадающей панели с серверами
            int righty = axisY;
            
            if (getY() != righty || getHeight() != righth)
            {
                setLocation(getX(), righty);
                setSize(getWidth(), righth);
                return;
            }
            
            int cut_size = (elements.length == 1 ? 8 : 10);
            BufferedImage d_pane = ImageUtils.splitImage(cut_size, cut_size, (elements.length * 8) + (elements.length * 7 - 7) + (paneY * 2) - 2, def_pane);
            g2d.drawImage(d_pane, 0, pre_line.getHeight() + paneY, null);
            g2d.setComposite(AlphaComposite.Src);
            
            for (int i = 0; i < elements.length; i++)
            {
                g2d.drawString(elements[i], 7, pre_line.getHeight() * (i + 1) + pre_line.getHeight() - (i * 4));
                if (frame != null && !frame.isOffline() && BaseUtils.getPropertyInt("server") == i) g2d.setColor(Color.decode(Style.program_combobox_sel_color));
                else g2d.setColor(Color.decode(Style.elements_text_color));
            }
            
            g2d.drawString(elements[selected], 7, (def_line.getHeight() - (g2d.getFontMetrics().getHeight() / 2)) + 1);
        } else if (entered)
        {
            int righth = rol_line.getHeight();
            if (getY() != axisY || getHeight() != righth)
            {
                setLocation(getX(), axisY);
                setSize(getWidth(), righth);
                return;
            }
            if (isEnabled()) g2d.drawImage(rol_line, 0, 0, w, rol_line.getHeight(), null);
            else g2d.drawImage(loc_line, 0, 0, w, loc_line.getHeight(), null);
            g2d.drawString(elements[selected], 7, (rol_line.getHeight() - (g2d.getFontMetrics().getHeight() / 2)) + 1);
        } else
        {
            int righth = def_line.getHeight();
            if (getY() != axisY || getHeight() != righth)
            {
                setLocation(getX(), axisY);
                setSize(getWidth(), righth);
                return;
            }
            if (isEnabled()) g2d.drawImage(def_line, 0, 0, w, def_line.getHeight(), null);
            else g2d.drawImage(loc_line, 0, 0, w, loc_line.getHeight(), null);
            g2d.drawString(elements[selected], 7, (def_line.getHeight() - (g2d.getFontMetrics().getHeight() / 2)) + 1);
        }
        
        if (Settings.draw_borders[0])
        {
            g2d.setColor(Color.GREEN);
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
        
        g2d.dispose();
    }
    
    public void mouseClicked(MouseEvent e)
    {
        if (!isEnabled()) return;
        
        boolean yTrue = false;
        
        if (y > pre_line.getHeight() + paneY)
        {
            y = (y - (pre_line.getHeight() + paneY + 2)) / 15;
            yTrue = true;
            selectValue = true;
        } else selectValue = false;
        
        if (pressed && yTrue && y < elements.length)
        {
            selected = y;
            if (frame != null) frame.panel.resetAlerts();
        }

        pressed = !pressed;
        repaint();
    }
    
    public void mouseMoved(MouseEvent e)
    {
        y = e.getY();
        repaint();
    }
    
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) { entered = false; repaint(); }
    public void mouseDragged(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) { entered = true; }
    public void mouseExited(MouseEvent e) { entered = false; repaint(); }
    
    public boolean isNotSelected()
    {
        if (cpanel != null)
        {
            return Settings.show_server_is_not_selected[0] && BaseUtils.getPropertyString("server") == null && getSelectedIndex() == 0;
        } else if (frame != null)
        {
            return getSelectedIndex() == 0;
        }
        
        return false;
    }
    
    public void turn()
    {
        pressed = false;
        repaint();
    }
    
    public void setSelectedIndex(int num)
    {
        if (num <= this.elements.length)
        {
            selected = num;
            repaint();
        }
    }
    
    public int getSelectedIndex()
    {
        return selected;
    }
    
    public boolean getSelectValue()
    {
        return selectValue;
    }
    
    public boolean getPressed()
    {
        return pressed;
    }
}