package ru.er_log.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import ru.er_log.Settings;
import ru.er_log.components.Frame;
import ru.er_log.java.eURLClassLoader;
import ru.er_log.utils.BaseUtils;
import ru.er_log.utils.GuardUtils;
import ru.er_log.utils.OperatingSystem;
import ru.er_log.utils.ThemeUtils;

public final class GameLauncher {
    
    public GameLauncher(String[] data) throws IOException
    {
        File gameDirectory = new File(BaseUtils.getClientDirectory());
        File binDirectory = new File(gameDirectory, "bin");
        File nativesDirectory = new File(gameDirectory, "bin/natives");
        File assetsDirectory = new File(gameDirectory, "assets");
        
        boolean version_above_1_7 = BaseUtils.versionCompare(BaseUtils.getClientVersion(), "1.7") == 1;
        boolean forge = Boolean.valueOf(Frame.frame.isOffline() ? ThemeUtils.offlineGameUtils.useForge(Frame.frame.serversList.getSelectedIndex()) : BaseUtils.getServerAbout()[4]);
        boolean liteloader = Boolean.valueOf(Frame.frame.isOffline() ? ThemeUtils.offlineGameUtils.useLiteLoader(Frame.frame.serversList.getSelectedIndex()) : BaseUtils.getServerAbout()[5]);
        
        String hAESKey, sesKey = null, UUID = null;
        
        try
        {
            hAESKey = GuardUtils.decrypt(Frame.onlineData[3], Settings.aes_key[0]);
            
            String stirSesKey = GuardUtils.decrypt(data[7], GuardUtils.reestablishString(hAESKey.split("<:k:>")[0], hAESKey.split("<:k:>")[1]));
            sesKey = GuardUtils.reestablishString(stirSesKey.split("<:k:>")[0], stirSesKey.split("<:k:>")[1]);
            
            String stirUUID = GuardUtils.decrypt(data[27], GuardUtils.reestablishString(hAESKey.split("<:k:>")[0], hAESKey.split("<:k:>")[1]));
            UUID = GuardUtils.reestablishString(stirUUID.split("<:k:>")[0], stirUUID.split("<:k:>")[1]);
        } catch (Exception e)
        {
            if (!Frame.frame.isOffline()) e.printStackTrace();
            else UUID = "0-0-0-0-0";
        }
        
        String username = Frame.frame.isOffline() ? Settings.off_user[0] : data[23];
        String session = Frame.frame.isOffline() ? (version_above_1_7 ? Settings.off_sess[0] : Settings.off_sess[0] + "<::>" + BaseUtils.getClientName() + "<::>" + GuardUtils.appPath() + "<::>null<::>" + GuardUtils.getHWID() + "<::>" + getLibraryList(binDirectory, forge, liteloader) + "<::>" + Settings.game_directory[0] + "<::>" + Settings.par_directory[0]) : (version_above_1_7 ? GuardUtils.md5(sesKey) : GuardUtils.md5(sesKey) + "<::>" + BaseUtils.getClientName() + "<::>" + GuardUtils.appPath() + "<::>" + data[20] + "<::>" + GuardUtils.getHWID() + "<::>" + getLibraryList(binDirectory, forge, liteloader) + "<::>" + Settings.game_directory[0] + "<::>" + Settings.par_directory[0]);
        
        List<String> properties = new ArrayList<>();
        List<String> tweak_args_list = new ArrayList<>();
        
        if (OperatingSystem.getCurrentPlatform().equals(OperatingSystem.OSX))
        {
            properties.add("-Xdock:icon=" + new File(assetsDirectory, "icons/minecraft.icns").getAbsolutePath());
            properties.add("-Xdock:name=Minecraft");
        }
        
        if (forge)
        {
            properties.add("-Dfml.ignoreInvalidMinecraftCertificates=true");
            properties.add("-Dfml.ignorePatchDiscrepancies=true");
        }
        
        properties.add("-Djava.library.path=" + nativesDirectory.toString());
        properties.add("-Dorg.lwjgl.librarypath=" + nativesDirectory.toString());
        properties.add("-Dnet.java.games.input.librarypath=" + nativesDirectory.toString());
        
        tweak_args_list.add("--username");       tweak_args_list.add(username);
        tweak_args_list.add("--version");        tweak_args_list.add(BaseUtils.getClientVersion());
        tweak_args_list.add("--gameDir");        tweak_args_list.add(gameDirectory.toString());
        tweak_args_list.add("--assetsDir");      tweak_args_list.add(assetsDirectory.toString());
        
        if (version_above_1_7)
        {
            if (!Frame.frame.isOffline())
            session = GuardUtils.md5(session + BaseUtils.lastNchars(session, 3)); // !!!
            
            tweak_args_list.add("--uuid");              tweak_args_list.add(UUID);
            tweak_args_list.add("--accessToken");       tweak_args_list.add(session);
            tweak_args_list.add("--userProperties");    tweak_args_list.add("{}");
            tweak_args_list.add("--assetIndex");        tweak_args_list.add(BaseUtils.getClientVersion());
        } else
        {
            tweak_args_list.add("--session");           tweak_args_list.add(session);
        }
        
        if (Frame.frame.set_full.isSelected())
        {
            tweak_args_list.add("--fullscreen"); tweak_args_list.add("true");
        }
        
        if (Settings.use_auto_entrance[0] && !Frame.frame.isOffline())
        {
            tweak_args_list.add("--server");     tweak_args_list.add(BaseUtils.getServerAbout()[1]);
            tweak_args_list.add("--port");       tweak_args_list.add(BaseUtils.getServerAbout()[2]);
        }
        
        if (forge || liteloader)
        {
            tweak_args_list.add("--tweakClass");
        }
        
        if (liteloader)
        {
            tweak_args_list.add("com.mumfrey.liteloader.launch.LiteLoaderTweaker");
        }
        
        if (forge && liteloader)
        {
            // Начиная с версии 1.6.4, клиент использует "--tweakClass" вместо "--cascadedTweaks", в качестве второстепенного класса
            String arg = BaseUtils.versionCompare(BaseUtils.getClientVersion(), "1.6.4") == 2 ? "--cascadedTweaks" : "--tweakClass";
            tweak_args_list.add(arg);
        }
        
        if (forge)
        {
            tweak_args_list.add("cpw.mods.fml.common.launcher.FMLTweaker");
        }
        
        if (GuardUtils.use_jar_check && !Frame.frame.isOffline())
        {
            Frame.report("GUARD: Проверка клиента на наличие сторонних JAR файлов...");
            GuardUtils.checkClientsJars(BaseUtils.getClientDirectory(), data);
            Frame.report("GUARD: Проверка завершена");
        }
        GuardUtils.checkClient(Frame.authData, false);
        
        if (GuardUtils.use_mod_check && GuardUtils.use_mod_check_timer && !Frame.frame.isOffline())
        {
            new Timer(GuardUtils.time_for_mods_check * 1000, new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    GuardUtils.checkClient(Frame.authData, false);
                }
            }).start();
        }
        
        setSystemProperties(properties);
        
        Frame.report("-------------------------");
        Frame.report("Переход к запуску игры...");
        Frame.report("-------------------------");
        
        Frame.frame.setAlert("Игра запущена", 1, 391, -1);
        BaseUtils.sleep(1.5);
        Frame.frame.setVisible(false);
        
        try
        {
            URL[] urls = toURLs(getLibraryList(binDirectory, forge, liteloader), getCPSeparator());
            eURLClassLoader loader = new eURLClassLoader(urls);
            BaseUtils.patchNewClientUrls(loader);
            
            String main_class = (forge || liteloader) ? Settings.lwrap_mine_class[0] : Settings.mine_class[0];
            Class cls = loader.loadClass(main_class);
            
            Method main = cls.getMethod("main", new Class[] { String[].class });
            main.invoke(null, new Object[] { tweak_args_list.toArray(new String[0]) });
            
        } catch (Exception e)
        {
            Frame.frame.setAlert("Ошибка в работе приложения", 3, 391, 2);
            Frame.frame.setVisible(true);
            
            Frame.reportErr("Ошибка при запуске или в работе приложения!");
            e.printStackTrace();
        }
    }
    
    private void setSystemProperties(List<String> properties)
    {
        Frame.report("Установка системных параметров...");
        
        for (String propertie : properties)
        {
            String[] key_value = propertie.replaceFirst("-D", "").replaceFirst("-X", "").split("=", 2);
            try
            {
                System.setProperty(key_value[0], key_value[1]);
            } catch (Exception e)
            {
                Frame.reportErr("Не удалось установить параметр: " + propertie);
                e.printStackTrace();
            }
        }
        Frame.report("Все системные параметры установлены");
    }
    
    private URL[] toURLs(String line, String sep)
    {
        String[] strs = line.split(sep);
        URL[] urls = new URL[strs.length];
        
        try
        {
            for (int i = 0; i < urls.length; i++)
                urls[i] = new File(strs[i]).toURI().toURL();
            
            return urls;
        } catch (MalformedURLException e)
        {
            Frame.reportErr("Ошибка при преобразовании в URL");
            e.printStackTrace();
        }
        
        return null;
    }
    
    private String getLibraryList(File bin_folder, boolean using_forge, boolean using_liteloader)
    {
        String sep = getCPSeparator();
        List<String> lib = new ArrayList<>();
        
        lib.add(bin_folder.toString() + File.separator + "libraries.jar");
        if (using_forge) lib.add(bin_folder.toString() + File.separator + "forge.jar");
        if (using_liteloader) lib.add(bin_folder.toString() + File.separator + "liteloader.jar");
        lib.add(bin_folder.toString() + File.separator + "minecraft.jar");
        lib.add(new File(GuardUtils.appPath()).toString());
        
        String library = "";
        for (int i = 0; i < lib.size(); i++)
        {
            library += lib.get(i);
            if (i + 1 < lib.size()) library += sep;
        }
        
        return library;
    }
    
    private String getCPSeparator()
    {
        return (BaseUtils.getPlatform() != 2) ? ":" : ";";
    }
}
