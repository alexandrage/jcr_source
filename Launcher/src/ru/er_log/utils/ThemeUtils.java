package ru.er_log.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import ru.er_log.Settings;
import ru.er_log.components.Button;
import ru.er_log.components.CheckBox;
import ru.er_log.components.ComboBox;
import ru.er_log.components.Frame;
import ru.er_log.components.LoginField;
import ru.er_log.components.Panel;
import ru.er_log.components.PassField;
import ru.er_log.components.STextField;
import ru.er_log.components.ThemeElements;
import static ru.er_log.components.ThemeElements.*;
import static ru.er_log.utils.BaseUtils.definitionFrames;
import static ru.er_log.utils.BaseUtils.startGame;
import static ru.er_log.Style.*;

public class ThemeUtils extends BaseUtils {

    public static Panel pb;
    public static ThemeUtils themeUtils;
    
    private static ComboBox saved_servers_list;
    private static int saved_servers_list_index = 0;
    private static boolean[] save_selected;
    private static String save_memory;
    
    public static OfflineGameUtils offlineGameUtils = new OfflineGameUtils();

    public static void updateStyle(Frame frame)
    {
        if (BaseUtils.getPlatform() != 0)
        {
            frame.turn = turn(turn, frame);
            frame.close = close(close);
        }
        
        frame.settings          = toSettings(frame.settings.getText());
        frame.doAuth            = doAuth(frame.doAuth.getText());
        
        frame.set_cancel        = cancelSettings(frame.set_cancel.getText());
        frame.set_take          = takeSettings(frame.set_take.getText());
        
        frame.skip              = skipSelfRenewal(frame.skip.getText());
        frame.take_self_renewal = takeSelfRenewal(frame.take_self_renewal.getText());
        
        frame.take              = actionAlertPane(frame.take.getText());
        frame.toGame            = toGame(frame.toGame.getText());
        
        frame.login             = new LoginField(field_login_X, field_login_Y, field_login_width, field_login_height, disableThemeColor, staticThemeColor);
        frame.password          = new PassField(field_password_X, field_password_Y, field_password_width, field_password_height, disableThemeColor, staticThemeColor);
        frame.serversList       = new ComboBox(getServersNames(), combobox_auth_servers_X, combobox_auth_servers_Y, frame);
        
        frame.set_remember      = new CheckBox(checkbox_remember_X, checkbox_remember_Y, checkbox_remember_width, checkbox_remember_height, frame.set_remember.getText(), true);
        frame.set_update        = new CheckBox(checkbox_redownload_X, checkbox_redownload_Y, checkbox_redownload_width, checkbox_redownload_height, frame.set_update.getText(), frame.set_update.isSelected());
        frame.set_full          = new CheckBox(checkbox_fullScreen_X, checkbox_fullScreen_Y, checkbox_fullScreen_width, checkbox_fullScreen_height, frame.set_full.getText(), frame.set_full.isSelected());
        frame.set_offline       = new CheckBox(checkbox_offline_X, checkbox_offline_Y, checkbox_offline_width, checkbox_offline_height, frame.set_offline.getText(), frame.isOffline());
        frame.set_memory        = new STextField(field_memory_X, field_memory_Y, field_memory_width, field_memory_height, frame.set_memory.getText(), 4, disableThemeColor, staticThemeColor);
    }

    public static JButton close(ImageUtils imgUt)
    {
        JButton button = new Button(imgUt, "");

        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        };
        button.addActionListener(action);

        return button;
    }

    public static JButton turn(ImageUtils imgUt, final JFrame frame)
    {
        JButton button = new Button(imgUt, "");

        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                frame.setState(Frame.ICONIFIED);
            }
        };
        button.addActionListener(action);

        return button;
    }

    public static JButton toSettings(String name)
    {
        JButton button = new Button(name, 1, button_settings_X, button_settings_Y, button_settings_width, button_settings_height);
        
        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Frame frame = Frame.frame; frame.toXFrame(2);
                save_selected = new boolean[] { frame.set_remember.isSelected(), frame.set_update.isSelected(), frame.set_full.isSelected(), frame.isOffline() };
                save_memory = frame.set_memory.getText();
                if (frame.isOffline()) saved_servers_list_index = frame.serversList.getSelectedIndex();
            }
        };
        button.addActionListener(action);

        return button;
    }
    
    public static JButton doAuth(String name)
    {
        JButton button = new Button(name, 2, button_auth_X, button_auth_Y, button_auth_width, button_auth_height);

        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if (StreamUtils.t != null && StreamUtils.t.isAlive() && StreamUtils.t.getName().equals("getServerOnline")) return;
                if (Frame.onlineData == null && !Frame.frame.isOffline()) return;
                
                Frame.frame.panel.resetAlerts();
                Frame.frame.serversList.turn();
                
                if (!Frame.frame.isOffline())
                {
                    if (!(Frame.frame.login.getText().equals(field_login_title)) && !(new String(Frame.frame.password.getPassword()).equals(field_password_title)))
                    { StreamUtils.doLogin(); }
                    else { Frame.frame.setAlert("Неверный логин или пароль", 3, 0); }
                } else if (offlineGameUtils.noClients())
                {
                    Frame.frame.setAlert("Используйте режим \"мультиплеер\"", 3, 0);
                } else if (Frame.frame.serversList.isNotSelected())
                {
                    Frame.frame.setAlert("Выберите клиент для игры", 3, 0);
                } else
                {
                    startGame(Frame.authData);
                }
            }
        };
        button.addActionListener(action);

        return button;
    }
    
    public static JButton cancelSettings(String name)
    {
        JButton button = new Button(name, 1, button_cancelSettings_X, button_cancelSettings_Y, button_cancelSettings_width, button_cancelSettings_height);

        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Frame frame = Frame.frame; frame.toXFrame(1);
                frame.set_remember.setSelected(save_selected[0]);
                frame.set_update.setSelected(save_selected[1]);
                frame.set_full.setSelected(save_selected[2]);
                frame.set_offline.setSelected(save_selected[3]);
                frame.set_memory.setText(save_memory);
                save_selected = null; save_memory = "";
            }
        };
        button.addActionListener(action);

        return button;
    }
    
    public static JButton takeSettings(String name)
    {
        JButton button = new Button(name, 2, button_takeSettings_X, button_takeSettings_Y, button_takeSettings_width, button_takeSettings_height);
        
        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                final Frame frame = Frame.frame; 
                
                if (frame.isOffline())
                {
                    saved_servers_list = frame.serversList;
                    frame.serversList = new ComboBox(offlineGameUtils.getClientsList(), combobox_auth_servers_X, combobox_auth_servers_Y, frame);
                    frame.serversList.setSelectedIndex(saved_servers_list_index);
                    frame.addServerOfflineMouseListener();
                    
                    frame.doAuth.setText(Settings.auth_but_offline_text[0]);
                } else if (saved_servers_list != null)
                {
                    frame.serversList = saved_servers_list;
                    frame.doAuth.setText(Settings.auth_but_auth_text[0]);
                }
                
                String smemory = frame.set_memory.getText();
                if (!save_memory.equals(smemory))
                {
                    int memory = 0;
                    try { memory = new Integer(smemory); } catch (Exception ex) {}
                    if (memory >= 256)
                    {
                        setProperty("memory", memory);
                        restart();
                    }
                    else { frame.set_memory.setText(save_memory); frame.toXFrame(1); }
                } else frame.toXFrame(1);
            }
        };
        button.addActionListener(action);

        return button;
    }
    
    public static JButton skipSelfRenewal(String name)
    {
        JButton button = new Button(name, 1, button_skip_X, button_skip_Y, button_skip_width, button_skip_height);

        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        };
        button.addActionListener(action);

        return button;
    }

    public static JButton takeSelfRenewal(String name)
    {
        JButton button = new Button(name, 2, button_takeSelfRrenewal_X, button_takeSelfRrenewal_Y, button_takeSelfRrenewal_width, button_takeSelfRrenewal_height);
        
        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                StreamUtils.actionTakeUpdate();
            }
        };
        button.addActionListener(action);

        return button;
    }
    
    public static JButton actionAlertPane(String name)
    {
        JButton button = new Button(name, 2, button_takeAlert_X, button_takeAlert_Y, button_takeAlert_width, button_takeAlert_height);
        
        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if (Frame.authData[8].equals("true") || BaseUtils.isUseDebuging())
                {
                    definitionFrames(Frame.frame);
                } else
                {
                    Frame.frame.toXFrame(3);
                }
            }
        };
        button.addActionListener(action);

        return button;
    }
    
    public static JButton toGame(String name)
    {
        final JButton button = new Button(name, 2, button_toGame_X, button_toGame_Y, button_toGame_width, button_toGame_height);
        
        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                button.setEnabled(false);
                if (Settings.use_loading_news[0])
                {
                    Panel.news_xcoord = ThemeElements.news_back.getWidth();
                    Frame.frame.panel.remove(Frame.frame.newsScPane);
                }
                
                BaseUtils.prepareUpdate();
            }
        };
        button.addActionListener(action);

        return button;
    }
}
