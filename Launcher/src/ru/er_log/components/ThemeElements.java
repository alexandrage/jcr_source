package ru.er_log.components;

import java.awt.Color;
import java.awt.image.BufferedImage;
import ru.er_log.utils.ImageUtils;
import static ru.er_log.Style.*;

public class ThemeElements {
    
    public static Color staticThemeColor;
    public static Color disableThemeColor;
    
    public static ImageUtils turn;
    public static ImageUtils close;
    
    public static BufferedImage background;
    public static BufferedImage news_back;
    public static BufferedImage sysButs;
    public static BufferedImage logotype;
    public static BufferedImage button;
    public static BufferedImage alertIcons;
    public static BufferedImage combobox;
    public static BufferedImage def_skin;
    public static BufferedImage bandColors;
    public static BufferedImage personal_alert;
    public static BufferedImage authFields;
    public static BufferedImage modalBack;
    public static BufferedImage waitIcon;
    public static BufferedImage progBarImage;
    public static BufferedImage pressBorder;
    public static BufferedImage checkbox;
    public static BufferedImage favicon;
    public static BufferedImage fieldBack;
    
    public static void initComponents()
    {
        // инициализация некоторых компонентов
        turn = new ImageUtils(button_turn_X, button_turn_Y, button_turn_width, button_turn_height, button_turn_def_X, button_turn_def_Y, button_turn_rol_X, button_turn_rol_Y, button_turn_pre_X, button_turn_pre_Y, sysButs);
        close = new ImageUtils(button_close_X, button_close_Y, button_close_width, button_close_height, button_close_def_X, button_close_def_Y, button_close_rol_X, button_close_rol_Y, button_close_pre_X, button_close_pre_Y, sysButs);
    }
}
