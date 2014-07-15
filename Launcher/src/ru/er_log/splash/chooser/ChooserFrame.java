package ru.er_log.splash.chooser;

import com.sun.awt.AWTUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import ru.er_log.Settings;
import ru.er_log.Starter;
import ru.er_log.Style;
import ru.er_log.components.Button;
import ru.er_log.components.CheckBox;
import ru.er_log.components.ComboBox;
import ru.er_log.components.Frame;
import ru.er_log.components.ThemeElements;
import ru.er_log.utils.BaseUtils;
import static ru.er_log.utils.BaseUtils.getTheme;
import static ru.er_log.utils.BaseUtils.openLocalImage;
import ru.er_log.utils.ImageUtils;
import ru.er_log.utils.ThemeUtils;

public class ChooserFrame extends JFrame implements ActionListener, KeyListener {
    
    public static ChooserFrame cframe;
    private final JPanel mainPanel = new JPanel();
    private final ChooserPanel panel = new ChooserPanel();
    private final ChooserStatusPanel stPanel = new ChooserStatusPanel();
    
    public ComboBox combobox;
    public CheckBox play_offline;
    public JButton turn_but;
    public JButton close_but;
    public JButton contain_but;
    
    private Thread thread = null;
    private int X = 0, Y = 0;
    
    boolean cant_enter = false;
    
    public ChooserFrame()
    {
        this.setUndecorated(true);
        if (BaseUtils.getPlatform() != 0) AWTUtilities.setWindowOpaque(this, false);
        this.setPreferredSize(new Dimension(Settings.chooser_width[0], Settings.chooser_height[0] + (Settings.use_monitoring[0] ? stPanel.getHeight() : 0)));
        this.setSize(this.getPreferredSize());
        this.setTitle(Settings.title[0] + " :: " + Settings.version[0]);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setIconImage(BaseUtils.openLocalImage(BaseUtils.getTheme().themeFavicon()));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout()); mouseListener();
        
        cant_enter = true;
        
        initNeedingImages();
        initNeedingComponents();
        
        combobox.setEnabled(false);
        loadServersList();
        
        mainPanel.setOpaque(false);
        mainPanel.setLayout(null);
        mainPanel.setFocusable(true);
        
            mainPanel.add(combobox);
            mainPanel.add(contain_but);
//            mainPanel.add(turn_but);
            mainPanel.add(close_but);
            
            mainPanel.add(panel);
            if (Settings.use_monitoring[0])
                mainPanel.add(stPanel);
            
        contain_but.addActionListener(this);
        mainPanel.addKeyListener(this);
        
        this.setContentPane(mainPanel);
    }
    
    private void loadServersList()
    {
        thread = new Thread()
        {
            public void run()
            {
                try { Thread.sleep(500); } catch (InterruptedException ex) {}
                String[] servers = BaseUtils.getServersNames();
                
                if (servers.length == 2 && servers[1].equals("error"))
                {
                    stPanel.add(play_offline);
                    stPanel.openPane();
                }
                
                combobox.reset(servers, Style.combobox_chooser_servers_X, Style.combobox_chooser_servers_Y, cframe);
                combobox.setEnabled(true);
                cant_enter = false;
                
                combobox.setSelectedIndex(BaseUtils.getPropertyInt("server"));

                if (Settings.use_monitoring[0] && !combobox.error)
                {
                    if (Settings.use_update_mon[0]) getServerOnline();
                    
                    combobox.addMouseListener(new MouseListener()
                    {
                        public void mouseReleased(MouseEvent e) {}
                        public void mousePressed(MouseEvent e) {}
                        public void mouseExited(MouseEvent e) {}
                        public void mouseEntered(MouseEvent e) {}
                        public void mouseClicked(MouseEvent e)
                        {
                            if (combobox.getPressed() || e.getButton() != MouseEvent.BUTTON1) return;

                            if (combobox.getSelectValue())
                            {
                                getServerOnline();
                            }
                        }
                    });
                }
            }
        };
        thread.setName("Chooser: getServersNames()");
        thread.start();
    }
    
    public void getServerOnline()
    {
        if (combobox.isNotSelected()) { stPanel.setStatus(". . .", 0); return; }
        if (thread != null) thread.stop();
        
        thread = new Thread() {
            public void run()
            {
                try
                {
                    stPanel.openPane();
                    stPanel.waitIcon(true);
                    
                    String server[] = BaseUtils.servers[combobox.getSelectedIndex()].split(" :: ");
                    String url = BaseUtils.getServerOnline(server);

                    if (url == null)
                    {
                        Frame.reportErr("Ошибка подключения к серверу: " + url);
                        stPanel.setStatus("Ошибка подключения", 2);
                    } else if (url.contains("<::>"))
                    {
                        String[] result = url.split("<::>");
                        if (new Integer(result[0]) >= new Integer(result[1]))
                        {
                            stPanel.setStatus("Сервер полон: " + result[0] + " из " + result[1], 3);
                        } else
                        {
                            stPanel.setStatus("Сервер доступен: " + result[0] + " из " + result[1], 1);
                        }
                    } else if (url.trim().equals("OFF"))
                    {
                        stPanel.setStatus("Сервер недоступен", 2);
                    } else if (url.trim().equals("TechWork"))
                    {
                        stPanel.setStatus("Технические работы", 3);
                    } else
                    {
                        Frame.reportErr("Внутренняя ошибка: " + url);
                        stPanel.setStatus("Внутренняя ошибка", 2);
                    }
                } catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        };
        thread.setName("Chooser: getServerOnline()");
        thread.start();
    }
    
    private void initNeedingImages()
    {
        ThemeElements.sysButs = openLocalImage(getTheme().themeSysButs());
        ThemeElements.combobox = openLocalImage(getTheme().themeComboBox());
        ThemeElements.checkbox = openLocalImage(getTheme().themeCheckBox());
        ThemeElements.alertIcons = openLocalImage(getTheme().themeAlertIcons());
        ThemeElements.waitIcon = openLocalImage(getTheme().themeWaitIcon());
    }
    
    private void initNeedingComponents()
    {
        contain_but = new Button(new ImageUtils(237, 10, 12, 10, 23, 0, 23, 10, 23, 20, ThemeElements.sysButs), "");
//        turn_but = ThemeUtils.turn(new ImageUtils(236, 10, 13, 10, 0, 0, 0, 10, 0, 20, ThemeElements.sysButs), this);
        close_but = ThemeUtils.close(new ImageUtils(260, 10, 10, 10, 13, 0, 13, 10, 13, 20, ThemeElements.sysButs));
        combobox = new ComboBox(new String[] { "Загрузка списка серверов..." }, Style.combobox_chooser_servers_X, Style.combobox_chooser_servers_Y, null);
        play_offline = new CheckBox((stPanel.getWidth() - Style.chooser_checkbox_width) / 2, (stPanel.getHeight() - Style.chooser_checkbox_height) / 2, Style.chooser_checkbox_width, Style.chooser_checkbox_height, "Режим оффлайн", Color.decode(Style.chooser_checkbox_color), false);
    }
    
    private void actionAfterTap()
    {
        if (thread != null) thread.interrupt();

        if (combobox.error && play_offline.isSelected())
        {
            BaseUtils.setProperty("offline_mode", true);
        } else if (combobox.error || cant_enter || combobox.isNotSelected())
        {
            return;
        } else
        {
            boolean b = Settings.show_server_is_not_selected[0] && BaseUtils.getPropertyString("server") == null;

            int index = combobox.getSelectedIndex();
            BaseUtils.setProperty("server", (b ? index - 1 : index));
            BaseUtils.setProperty("server_name", BaseUtils.servers[index].split(" :: ")[0]);
            BaseUtils.setProperty("offline_mode", false);

            combobox.turn();
            combobox.setEnabled(false);
        }

        cant_enter = true;

        try
        {
            Starter.launchProgramAndContinue(cframe);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyText(e.getKeyCode()).equals("Space") || e.getKeyText(e.getKeyCode()).equals("Enter"))
        {
            actionAfterTap();
        }
    }
    
    private void mouseListener()
    {
        if (BaseUtils.getPlatform() == 0) return;
        
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
    
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == contain_but)
        {
            actionAfterTap();
        }
    }
}
