package ru.er_log.components;

import com.sun.awt.AWTUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import ru.er_log.Settings;
import ru.er_log.Starter;
import ru.er_log.splash.state.SplashFrame;
import ru.er_log.utils.EncodingUtils;
import ru.er_log.utils.GuardUtils;
import ru.er_log.utils.ImageUtils;
import ru.er_log.utils.StreamUtils;
import ru.er_log.utils.ThemeUtils;
import static ru.er_log.utils.BaseUtils.*;
import static ru.er_log.utils.ThemeUtils.offlineGameUtils;
import static ru.er_log.Style.*;

/**
 *
 * @author Eldar T. (CRaFT4ik)
 * 
 */
public class Frame extends JFrame implements FocusListener, KeyListener {
    
    public static String[] authData = null;
    public static String[] onlineData = new String[] { "" };
    private static boolean offlineMode = false;
    private static int debLine = 1;
    
    public JButton turn  = new JButton();
    public JButton close = new JButton();
    private int X = 0, Y = 0;
    
    public static Frame frame;
    public GuardUtils gu = new GuardUtils();
    public Panel panel = new Panel();
        public JLabel skin               = new JLabel();
        public JLabel cloak              = new JLabel();
        public JLabel logotype           = new JLabel(new ImageIcon(ThemeElements.logotype));
        public ComboBox serversList      = null;
        public JTextPane news_pane       = new JTextPane();
        public JScrollPane newsScPane    = new JScrollPane(news_pane);
        public JTextField login          = new JTextField();
        public JPasswordField password   = new JPasswordField();
        public JButton toGame            = new JButton(button_toGame_title);
        public JButton take              = new JButton(button_takeAlert_title);
        public JButton skip              = new JButton(button_skip_title);
        public JButton take_self_renewal = new JButton(button_takeSelfRrenewal_title);
        public JButton doAuth            = new JButton(button_auth_title);
        public JButton settings          = new JButton(button_settings_title);
            public JButton set_cancel     = new JButton(button_cancelSettings_title);
            public JButton set_take       = new JButton(button_takeSettings_title);
            public JCheckBox set_remember = new JCheckBox("Запомнить мои данные");
            public JCheckBox set_update   = new JCheckBox("Перекачать клиент");
            public JCheckBox set_full     = new JCheckBox("Полноэкранный режим");
            public JCheckBox set_offline  = new JCheckBox("Режим оффлайн");
            public JTextField set_memory  = new JTextField("32".equals(System.getProperty("sun.arch.data.model")) ? "512" : "1024");
            
        
    public Frame()
    {
        if (getPlatform() != 0)
        {
            this.setUndecorated(true); AWTUtilities.setWindowOpaque(this, false);
            this.setPreferredSize(new Dimension(Settings.frame_width[0], Settings.frame_height[0]));
        } else this.setPreferredSize(new Dimension(Settings.frame_width[0] + Settings.linux_frame_w[0], Settings.frame_height[0] + Settings.linux_frame_h[0]));
        this.setSize(this.getPreferredSize());
        this.setTitle(Settings.title[0] + " :: " + Settings.version[0]);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setIconImage(ThemeElements.favicon); mouseListener();
        try { ThemeUtils.updateStyle(this); } catch(Exception e) {}
        
            login.setText(field_login_title);
            login.addActionListener(null);
            login.addFocusListener(this);
            
            password.setText(field_password_title);
            password.addActionListener(null);
            password.addFocusListener(this);
            
            news_pane.setOpaque(false);
            news_pane.setBorder(null);
            news_pane.setContentType("text/html");
            news_pane.setEditable(false);
            news_pane.setFocusable(false);
            news_pane.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent e)
                {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                    openLink(e.getURL().toString());
                }
            });
            
            newsScPane.setOpaque(false);
            newsScPane.getViewport().setOpaque(false);
            newsScPane.setBorder(null);
            newsScPane.setBounds(Settings.frame_width[0] + 25, 10 + 35, ThemeElements.news_back.getWidth() - 25 * 2, 430 - 35 * 2);

            offlineMode = getPropertyBoolean("offline_mode");

            if (!Settings.allow_to_change_server[0])
            {
                serversList.setEnabled(false);
                serversList.setSelectedIndex(getPropertyInt("server") + 1);
            }

            if (isOffline())
            {
                setOffline(true);
                set_offline.setEnabled(false);
                serversList = new ComboBox(offlineGameUtils.getClientsList(), combobox_auth_servers_X, combobox_auth_servers_Y, this);
                addServerOfflineMouseListener();
                serversList.setSelectedIndex(getPropertyInt("offline_server"));
                setProperty("offline_mode", false);
                setProperty("offline_server", 0);
            } else
            {
                serversList.addMouseListener(new MouseListener()
                {
                    public void mouseReleased(MouseEvent e) {}
                    public void mousePressed(MouseEvent e) {}
                    public void mouseExited(MouseEvent e) {}
                    public void mouseEntered(MouseEvent e) {}
                    public void mouseClicked(MouseEvent e)
                    {
                        if (!serversList.error)
                        {
                            if (serversList.getPressed() || e.getButton() != MouseEvent.BUTTON1 ||
                                    !serversList.getSelectValue() || serversList.isNotSelected()) return;

                            if (set_remember.isSelected())
                            {
                                setProperty("login", EncodingUtils.encode(login.getText()));
                                if (Settings.use_pass_remember[0])
                                    setProperty("password", EncodingUtils.encode(new String(password.getPassword())));
                            } else
                            {
                                deleteProperty("login");
                                if (Settings.use_pass_remember[0])
                                    deleteProperty("password");
                            }
                            
                            int server_index = serversList.getSelectedIndex();
                            setProperty("server", server_index - 1);
                            setProperty("server_name", servers[server_index].split(" :: ")[0]);
                            
                            restart();
                        }
                    }
                });
            }
            
            logotype.setBounds((Settings.frame_width[0] - ThemeElements.logotype.getWidth()) / 2, Settings.logo_indent[0], ThemeElements.logotype.getWidth(), ThemeElements.logotype.getHeight());
            if (Settings.use_logo_as_url[0]) logotype.addMouseListener(new MouseListener()
            {
                public void mouseReleased(MouseEvent e) {}
                public void mousePressed(MouseEvent e) {}
                public void mouseExited(MouseEvent e) { setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); }
                public void mouseEntered(MouseEvent e) { setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
                public void mouseClicked(MouseEvent e) { openLink("http://" + (Settings.logo_url[0].isEmpty() ? Settings.domain[0] : Settings.logo_url[0])); }
            });
            
          panel.addKeyListener(this);
          login.addKeyListener(this);
          password.addKeyListener(this);
          
          addFrameElements(false);
          addAuthElements(false);
          
        this.setContentPane(panel);
    }
    
    public static void beforeStart(SplashFrame sframe)
    {
        report("Запуск JCR Launcher " + Settings.version[0]);
        try
        {
            if (sframe!= null) sframe.setStatus("установка системного LnF...");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            report("Установка системного LnF успешно завершена");
        } catch (Exception e)
        { report("Не удалось установить системный LnF"); }
        
        onlineData = baseUtils.loadOnlineSettings(sframe);
        GuardUtils.setGuardData();
        
        if (sframe!= null) sframe.setStatus("загрузка изображений...");
        setTheme();
    }
    
    public static void start(final SplashFrame sframe)
    {
        frame = new Frame();
        frame.panel.setAuthState();
        
        boolean Internet_connected = !(frame.serversList == null);
        
        readConfig(frame);
        if (Settings.use_loading_news[0])
        {
            Color c = ThemeElements.staticThemeColor;
            StreamUtils.loadNewsPage(getURLSc("jcr_news.php?color=" + c.getRed() + "," + c.getGreen() + "," + c.getBlue()));
        }
        
        if (Settings.use_update_mon[0] && Internet_connected)
        {
            frame.panel.waitIcon(true, 0);
            StreamUtils.getServerOnline();
        }
        
        SwingUtilities.invokeLater(new Runnable()
        { public void run() { if (sframe != null) sframe.dispose(); } });
        
        frame.setVisible(true);
        
        if (GuardUtils.use_process_check && Internet_connected)
        {
            if (GuardUtils.checkProcesses(onlineData, true))
            {
                frame.elEnabled(false);
                frame.setAlert("Завершите сторонние процессы", 3, 0);
                reportErr("В системе обнаружены запрещенные запущенные процессы");
                return;
            }
            
            if (GuardUtils.use_process_check_timer)
            {
                new Timer(GuardUtils.time_for_process_check * 1000, new ActionListener() {
                    public void actionPerformed(ActionEvent e)
                    {
                        if (GuardUtils.checkProcesses(onlineData, false))
                        {
                            reportErr("В системе обнаружены запрещенные запущенные процессы");
                            reportErr("Завершение работы программы...");
                            System.exit(1);
                        }
                    }
                }).start();
            }
        }
    }
    
    public final void addServerOfflineMouseListener()
    {
        serversList.addMouseListener(new MouseListener()
        {
            public void mouseReleased(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseClicked(MouseEvent e)
            {
                if (!offlineGameUtils.noClients())
                {
                    if (serversList.getPressed() || e.getButton() != MouseEvent.BUTTON1 ||
                            !serversList.getSelectValue() || serversList.isNotSelected()) return;
                    
                    restart();
                } else
                {
                    serversList.error = true;
                }
            }
        });
    }
    
    private void mouseListener()
    {
        if (getPlatform() == 0) return;
        
        this.addMouseMotionListener(new MouseMotionAdapter()
        {
            public void mouseDragged(MouseEvent e)
            {
                setLocation(getX() + e.getX() - X, getY() + e.getY() - Y);
            }
        });
        this.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                X = e.getX();
                Y = e.getY();
            }
        });
    }
    
    public void elEnabled(boolean enable)
    {
        if (Settings.allow_to_change_server[0])
            serversList.setEnabled(enable);
        login.setEnabled(enable);
        password.setEnabled(enable);
        doAuth.setEnabled(enable);
        take.setEnabled(enable);
        settings.setEnabled(enable);
//        exit.setEnabled(enable);
        take_self_renewal.setEnabled(enable);
        set_cancel.setEnabled(enable);
        set_take.setEnabled(enable);
        set_remember.setEnabled(enable);
        set_update.setEnabled(enable);
        set_full.setEnabled(enable);
//        set_offline.setEnabled(enable);
        set_memory.setEnabled(enable);
        login.setEnabled(enable);
        password.setEnabled(enable);
    }
    
    public void toXFrame(int type)
    {
        BufferedImage min = ImageUtils.takePicture(panel).getSubimage(0, 0, panel.getWidth(), panel.getHeight());
        panel.removeAll();
        Panel.animation.paneAttenuation(min, type);
        panel.hideAlerts(true);
        addFrameElements(false);
        repaint();
    }
    
    public void paneState(int goTo)
    {
        switch (goTo)
        {
            case 1: panel.setAuthState(); break;
            case 2: panel.setSettings(); break;
            case 3: panel.setUpdateState(); break;
            case 4: panel.setGameUpdateState(); break;
            case 5: panel.setPersonalState(); break;
            case 6: panel.setAlertState(); break;
        }
    }
    
    public void afterFilling(final int aF, boolean remove)
    {
        switch (aF)
        {
            case 1: addAuthElements(remove); break;
            case 2: addSettingsElements(remove); break;
            case 3: addUpdateElements(remove); break;
            case 4: break;
            case 5: addPersonalElements(remove); break;
            case 6: addAlertPaneElements(remove); break;
        }
    }
    
    protected final void addFrameElements(boolean remove)
    {
        if(!remove)
        {
            panel.add(turn);
            panel.add(close);
        } else
        {
            panel.remove(turn);
            panel.remove(close);
        }
    }
    
    protected final void addAuthElements(boolean remove)
    {
        if (!remove)
        {
            panel.add(logotype);
            panel.add(serversList);
            panel.add(doAuth);
            panel.add(settings);
            panel.add(login);
            panel.add(password);
        } else
        {
            panel.remove(logotype);
            panel.remove(serversList);
            panel.remove(doAuth);
            panel.remove(settings);
            panel.remove(login);
            panel.remove(password);
        }
    }
    
    protected void addSettingsElements(boolean remove)
    {
        if (!remove)
        {
            panel.add(set_cancel);
            panel.add(set_take);
            panel.add(set_remember);
            panel.add(set_update);
            panel.add(set_full);
            panel.add(set_offline);
            panel.add(set_memory);
        } else
        {
            panel.remove(set_cancel);
            panel.remove(set_take);
            panel.remove(set_remember);
            panel.remove(set_update);
            panel.remove(set_full);
            panel.remove(set_offline);
            panel.remove(set_memory);
        }
    }
    
    protected void addUpdateElements(boolean remove)
    {
        if(!remove)
        {
            panel.add(skip);
            panel.add(take_self_renewal);
        } else
        {
            panel.remove(skip);
            panel.remove(take_self_renewal);
        }
    }
    
    protected void addPersonalElements(boolean remove)
    {
        if(!remove)
        {
            panel.add(skin);
            panel.add(cloak);
            panel.add(skip);
            panel.add(toGame);
        } else
        {
            panel.remove(skin);
            panel.remove(cloak);
            panel.remove(skip);
            panel.remove(toGame);
        }
    }
    
    protected void addAlertPaneElements(boolean remove)
    {
        if(!remove)
        {
            panel.add(skip);
            panel.add(take);
        } else
        {
            panel.remove(skip);
            panel.remove(take);
        }
    }
    
    public void setAlert(String alert, int type, int in_frame)
    {
        panel.alertIcons(alert, type, in_frame);
    }
    
    public void setAlert(String alert, int type, int y, int in_frame)
    {
        panel.alertIcons(alert, type, y, in_frame);
    }
    
    public static final boolean isOffline()
    {
        if (frame != null)
            return frame.set_offline.isSelected() || offlineMode || getPropertyBoolean("offline_mode");
        else
            return offlineMode || getPropertyBoolean("offline_mode");
    }
    
    public final void setOffline(boolean offline)
    {
        offlineMode = offline;
        set_offline.setSelected(offline);
    }
    
    public static void report(String mes)
    {
        if(Settings.use_debugging[0])
        {
            String num;
            if(Integer.toString(debLine).length() == 1) num = "00" + debLine + ": ";
            else if(Integer.toString(debLine).length() == 2) num = "0" + debLine + ": ";
            else if(Integer.toString(debLine).length() == 3) num = debLine + ": ";
            else num = "999: ";
            
            System.out.println((Starter.isStarted() ? "" : num) + mes);
            debLine++;
        }
    }
    
    public static void reportErr(String errMes)
    {
        if(Settings.use_debugging[0])
        {
            String num = null;
            if(Integer.toString(debLine).length() == 1) num = "00" + debLine + ": ";
            else if(Integer.toString(debLine).length() == 2) num = "0" + debLine + ": ";
            else if(Integer.toString(debLine).length() == 3) num = debLine + ": ";
            else num = "999: ";
            
            System.err.println((Starter.isStarted() ? "" : num) + errMes);
            debLine++;
        }
    }
    
    public void focusGained(FocusEvent e)
    {
        if(e.getSource() == login && login.getText().equals(field_login_title)) login.setText("");
        if(e.getSource() == password && new String(password.getPassword()).equals(field_password_title)) password.setText("");
    }

    public void focusLost(FocusEvent e)
    {
        if(e.getSource() == login && login.getText().equals("")) login.setText(field_login_title);
        if(e.getSource() == password && new String(password.getPassword()).equals("")) password.setText(field_password_title);
    }
    
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyText(e.getKeyCode()).equals("Enter"))
        {
            if (panel.unit == 0) doAuth.doClick();
            else if (panel.unit == 4) toGame.doClick();
        }
    }
}
