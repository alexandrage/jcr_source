package ru.er_log.components;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import ru.er_log.Settings;
import ru.er_log.utils.BaseUtils;
import ru.er_log.utils.ImageUtils;
import static ru.er_log.utils.ImageUtils.*;
import ru.er_log.utils.PersonalFilter;
import ru.er_log.utils.StreamUtils;

public class PersonalCab {
    
    public static BufferedImage skinImage;
    public static BufferedImage cloakImage;
    private static boolean failed_to_load_skin, failed_to_load_cloak;
    private static int skin_attempts_num = 0, cloak_attempts_num = 0;
    
    public static void draw(Graphics2D g2d)
    {
        boolean show = false;
        try { show = Boolean.valueOf(Frame.authData[25]); } catch (Exception e) {}
        if (show) g2d.drawImage(ThemeElements.personal_alert, 57, 65, null);
        
        g2d.drawImage(splitImage(104, 189, ThemeElements.pressBorder), 65, 98, null);
        g2d.drawImage(splitImage(74, 104, ThemeElements.pressBorder), 207, 98, null);
        
        if (Settings.draw_borders[0])
        {
            g2d.drawRect(77, 110, 80, 165);
            g2d.drawRect(219, 110, 50, 80);
        }
    }
    
    public static void refreshImage(String img_path, final int type, Frame frame)
    {
        BufferedImage img = BaseUtils.openImage(img_path);

        if (type == 0)
        {
            frame.skin.setIcon(new ImageIcon(assembleSkin(img, 5)));
        } else
        {
            frame.cloak.setIcon(new ImageIcon(assembleCloak(img, 5)));
        }
    }
    
    public static void setImages(String[] data, Frame frame)
    {
        try
        {
            String skins_url = (data[11].startsWith("http:") | data[11].startsWith("https:")) ? data[11] : BaseUtils.getURLFi("skins/") + data[11];
            String cloaks_url = (data[12].startsWith("http:") | data[12].startsWith("https:")) ? data[12] : BaseUtils.getURLFi("cloaks/") + data[12];
            
            Frame.report("Загрузка и установка скина...");
            skinImage = ImageUtils.getImageByURL(new URL(skins_url), BaseUtils.isUseDebuging());
            
            Frame.report("Загрузка и установка плаща...");
            cloakImage = ImageUtils.getImageByURL(new URL(cloaks_url), BaseUtils.isUseDebuging());
            
            identifyErrors(data, frame);
            
            frame.skin = new JLabel(new ImageIcon(assembleSkin(skinImage, 5)));
            frame.cloak = new JLabel(new ImageIcon(assembleCloak(cloakImage, 5)));
            
            actionOnEnteringToFile(frame);
            
        } catch (MalformedURLException e) {}
    }
    
    private static void identifyErrors(String[] data, Frame frame)
    {
        if (skinImage == null && cloakImage == null && !data[11].toLowerCase().contains("default.png") && !data[12].toLowerCase().contains("default.png"))
        {
            failed_to_load_skin = true; failed_to_load_cloak = true;
            
            frame.setAlert("Не удалось загрузить скин и плащ", 3, 391, 0);
            frame.panel.waitIcon(false, 391, 0);
            BaseUtils.sleep(2.0);
        } else if (skinImage == null && !data[11].toLowerCase().contains("default.png"))
        {
            failed_to_load_skin = true;
            
            frame.setAlert("Не удалось загрузить скин", 3, 391, 0);
            frame.panel.waitIcon(false, 391, 0);
            BaseUtils.sleep(2.0);
        } else if (cloakImage == null && !data[12].toLowerCase().contains("default.png"))
        {
            failed_to_load_cloak = true;
            
            frame.setAlert("Не удалось загрузить плащ", 3, 391, 0);
            frame.panel.waitIcon(false, 391, 0);
            BaseUtils.sleep(2.0);
        }
        
        if (skinImage == null) skinImage = new BufferedImage(64, 32, 2);
        if (cloakImage == null) cloakImage = new BufferedImage(22, 17, 2);
    }
    
    private static void actionOnEnteringToFile(final Frame frame)
    {
        boolean use_uploading_images = false;
        try { use_uploading_images = Boolean.valueOf(Frame.authData[25]); } catch (Exception e) {}
        
        boolean can_uploading_cloak = false;
        try { can_uploading_cloak = Boolean.valueOf(Frame.authData[26]); } catch (Exception e) {}
        final boolean uploading_cloak = can_uploading_cloak;
        
        frame.skin.setBounds(77, 110, frame.skin.getIcon().getIconWidth(), frame.skin.getIcon().getIconHeight());
        if (use_uploading_images) frame.skin.addMouseListener(new MouseListener()
        {
            public void mouseReleased(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseExited(MouseEvent e) { frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); }
            public void mouseEntered(MouseEvent e) { frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
            public void mouseClicked(MouseEvent e)
            {
                if (skin_attempts_num >= Settings.max_attempts_num[0])
                {
                    frame.panel.resetAlerts();
                    frame.setAlert("Число загрузок максимально", 3, 400, 4);
                } else if (!failed_to_load_skin)
                {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileFilter(new PersonalFilter(0));
                    chooser.setAcceptAllFileFilterUsed(false);

                    if (chooser.showDialog(frame, "Загрузить скин") == JFileChooser.APPROVE_OPTION)
                    {
                        StreamUtils.uploadFile(chooser.getSelectedFile(), 0, frame);
                        skin_attempts_num++;
                    }
                } else
                {
                    frame.panel.resetAlerts();
                    frame.setAlert("Невозможно установить скин", 3, 400, 4);
                    Frame.reportErr("Личный кабинет: Невозможно установить скин по причине ошибки загрузки и отображения ранее установленного");
                }
            }
        });
        
        frame.cloak.setBounds(219, 110, frame.cloak.getIcon().getIconWidth(), frame.cloak.getIcon().getIconHeight());
        if (use_uploading_images) frame.cloak.addMouseListener(new MouseListener()
        {
            public void mouseReleased(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseExited(MouseEvent e) { frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); }
            public void mouseEntered(MouseEvent e) { frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
            public void mouseClicked(MouseEvent e)
            {
                if (!uploading_cloak)
                {
                    frame.panel.resetAlerts();
                    frame.setAlert("Недостаточно прав для загрузки", 3, 400, 4);
                    Frame.report("Недостаточно прав для загрузки плаща");
                } else if (cloak_attempts_num >= Settings.max_attempts_num[0])
                {
                    frame.panel.resetAlerts();
                    frame.setAlert("Число загрузок максимально", 3, 400, 4);
                } else if (!failed_to_load_cloak)
                {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileFilter(new PersonalFilter(1));
                    chooser.setAcceptAllFileFilterUsed(false);

                    if (chooser.showDialog(frame, "Загрузить плащ") == JFileChooser.APPROVE_OPTION)
                    {
                        StreamUtils.uploadFile(chooser.getSelectedFile(), 1, frame);
                        cloak_attempts_num++;
                    }
                } else
                {
                    frame.panel.resetAlerts();
                    frame.setAlert("Невозможно установить плащ", 3, 400, 4);
                    Frame.reportErr("Личный кабинет: Невозможно установить плащ по причине ошибки загрузки и отображения ранее установленного");
                }
            }
        });
    }
    
    public static BufferedImage assembleSkin(BufferedImage img, final int xN)
    {
        if (img == null) img = ThemeElements.def_skin;
        
        int w = img.getWidth() / 64, h = img.getHeight() / 32;
        BufferedImage fullImg = new BufferedImage(16 * xN, 32 * xN + xN, 2);
        Graphics g = fullImg.getGraphics();
        
        g.drawImage(img.getSubimage(w * 8, h * 8, w * 8, h * 8), 4 * xN, xN, 8 * xN, 8 * xN, null); 					// Голова
        g.drawImage(img.getSubimage(w * 20, h * 20, w * 8, h * 12), 4 * xN, 8 * xN + xN, 8 * xN, 12 * xN, null); 			// Туловище
        g.drawImage(img.getSubimage(w * 44, h * 20, w * 4, h * 12), 0, 8 * xN + xN, 4 * xN, 12 * xN, null); 				// Левая рука
        g.drawImage(ImageUtils.flipImage(img.getSubimage(w * 44, h * 20, w * 4, h * 12)), 12 * xN, 8 * xN + xN, 4 * xN, 12 * xN, null); // Правая рука
        g.drawImage(img.getSubimage(w * 4, h * 20, w * 4, h * 12), 4 * xN, 20 * xN + xN, 4 * xN, 12 * xN, null); 			// Левая нога
        g.drawImage(ImageUtils.flipImage(img.getSubimage(w * 4, h * 20, w * 4, h * 12)), 8 * xN, 20 * xN + xN, 4 * xN, 12 * xN, null); 	// Правая нога
        g.drawImage(img.getSubimage(w * 40, h * 8, w * 8, h * 8), (4 - 1) * xN, 0, (8 + 2) * xN, (8 + 2) * xN, null); 			// Головной убор

        return fullImg;
    }
    
    public static BufferedImage assembleCloak(BufferedImage img, final int xN)
    {
        if (img == null) return null;
        
        BufferedImage fullImg = new BufferedImage(10 * xN, 16 * xN, 2);
        
        int w = img.getWidth(), h = img.getHeight();
        if (img.getWidth() % 64 == 0 && img.getHeight() % 32 == 0) { w /= 64; h /= 32; }
        else { w /= 22; h /= 17; }
        
        fullImg.getGraphics().drawImage(img.getSubimage(w, h, w * 10, h * 16), 0, 0, 10 * xN, 16 * xN, null);
        
        return fullImg;
    }
}
