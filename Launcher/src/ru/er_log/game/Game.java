package ru.er_log.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.Timer;
import net.minecraft.Launcher;
import ru.er_log.Settings;
import ru.er_log.components.Frame;
import ru.er_log.utils.BaseUtils;
import ru.er_log.utils.GuardUtils;
import static ru.er_log.utils.BaseUtils.getClientDirectory;

public class Game extends JFrame {

    public static Launcher applet;

    public Game(final String[] data)
    {
        String hAESKey, sesKey = null;
        
        try
        {
            hAESKey = GuardUtils.decrypt(Frame.onlineData[3], Settings.aes_key[0]);
            
            String stirSesKey = GuardUtils.decrypt(data[7], GuardUtils.reestablishString(hAESKey.split("<:k:>")[0], hAESKey.split("<:k:>")[1]));
            sesKey = GuardUtils.reestablishString(stirSesKey.split("<:k:>")[0], stirSesKey.split("<:k:>")[1]);
        } catch (Exception e)
        {
            if (!Frame.frame.isOffline()) e.printStackTrace();
        }
        
        String username = Frame.frame.isOffline() ? Settings.off_user[0] : data[23];
        String session_num = Frame.frame.isOffline() ? Settings.off_sess[0] + "<::>" + BaseUtils.getClientName() + "<::>" + GuardUtils.appPath() + "<::>null<::>" + GuardUtils.getHWID() + "<::>" + getLibraryList() + "<::>" + Settings.game_directory[0] + "<::>" + Settings.par_directory[0] : GuardUtils.md5(sesKey) + "<::>" + BaseUtils.getClientName() + "<::>" + GuardUtils.appPath() + "<::>" + data[20] + "<::>" + GuardUtils.getHWID() + "<::>" + getLibraryList() + "<::>" + Settings.game_directory[0] + "<::>" + Settings.par_directory[0];
        
        if (GuardUtils.use_jar_check && !Frame.frame.isOffline())
        {
            Frame.report("GUARD: Проверка клиента на наличие сторонних JAR файлов...");
            GuardUtils.checkClientsJars(getClientDirectory(), data);
            Frame.report("GUARD: Проверка завершена");
        }
        GuardUtils.checkOldClient(Frame.authData, true);
        
        if (GuardUtils.use_mod_check && GuardUtils.use_mod_check_timer && !Frame.frame.isOffline())
        {
            new Timer(GuardUtils.time_for_mods_check * 1000, new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    GuardUtils.checkOldClient(Frame.authData, false);
                }
            }).start();
        }
        
        try
        {
            addWindowListener(new WindowListener() {
                public void windowOpened(WindowEvent e) {}
                public void windowIconified(WindowEvent e) {}
                public void windowDeiconified(WindowEvent e) {}
                public void windowDeactivated(WindowEvent e) {}
                public void windowClosed(WindowEvent e) {}
                public void windowActivated(WindowEvent e) {}
                public void windowClosing(WindowEvent e)
                {
                    applet.stop();
                    applet.destroy();
                    System.exit(0);
                }
            });

            String bin = BaseUtils.getClientDirectory() + File.separator + "bin" + File.separator;
            setForeground(Color.BLACK);
            setBackground(Color.BLACK);
            URL[] urls = new URL[5];
            urls[0] = new File(bin, "minecraft.jar").toURI().toURL();
            urls[1] = new File(bin, "lwjgl.jar").toURI().toURL();
            urls[2] = new File(bin, "jinput.jar").toURI().toURL();
            urls[3] = new File(bin, "lwjgl_util.jar").toURI().toURL();
            urls[4] = new File(GuardUtils.appPath()).toURI().toURL();

            applet = new Launcher(bin, urls);
            applet.customParameters.put("username", username);
            applet.customParameters.put("sessionid", session_num);
            applet.customParameters.put("stand-alone", "true");
            if (Settings.use_auto_entrance[0] && !Frame.frame.isOffline())
            {
                applet.customParameters.put("server", BaseUtils.getServerAbout()[1]);
                applet.customParameters.put("port", BaseUtils.getServerAbout()[2]);
            }
            
            setTitle(Settings.title_in_game[0]);
            Frame.frame.setVisible(false);
            setSize(880, 520);
            setMinimumSize(new Dimension(880, 520));
            setLocationRelativeTo(null);
            
            applet.setForeground(Color.BLACK);
            applet.setBackground(Color.BLACK);
            setLayout(new BorderLayout());
            add(applet, BorderLayout.CENTER);
            validate();
            if (Frame.frame.set_full.isSelected())
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            setIconImage(BaseUtils.openLocalImage(BaseUtils.getTheme().themeGameIcon()));
            setVisible(true);

            if (!Settings.use_game_debug_mode[0])
            {
                System.setErr(new PrintStream(new NulledStream()));
                System.setOut(new PrintStream(new NulledStream()));
            }
            applet.init();
            applet.start();
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }
    
    private String getLibraryList()
    {
        String sep = ";";
        if (BaseUtils.getPlatform() != 2) sep = ":";
        
        String bin_path = getClientDirectory() + File.separator + "bin" + File.separator;
        
        String library =
                bin_path + "minecraft.jar" + sep +
                bin_path + "lwjgl.jar" + sep +
                bin_path + "jinput.jar" + sep +
                bin_path + "lwjgl_util.jar" + sep +
                new File(GuardUtils.appPath()).toString()
                ;
        
        return library;
    }
}

class NulledStream extends OutputStream {

    public void write(int b) throws IOException {}
}
