package ru.er_log.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import net.minecraft.Launcher;
import ru.er_log.Settings;
import ru.er_log.Starter;
import ru.er_log.components.Frame;
import ru.er_log.components.PersonalCab;
import ru.er_log.components.ThemeElements;
import ru.er_log.components.UI_Theme;
import ru.er_log.game.Game;
import ru.er_log.game.GameLauncher;
import ru.er_log.game.GameUpdater;
import ru.er_log.java.eURLClassLoader;
import ru.er_log.splash.state.SplashFrame;

public class BaseUtils {

    public static Launcher launcher;
    public static GameUpdater gameUpdater;
    public static BaseUtils baseUtils = new BaseUtils();
    public static final ConfigUtils config = new ConfigUtils("config", getParentDirectory());
    
    public static String[] servers = null;
    public static boolean offlineTheme = false;
    
    public static BufferedImage openLocalImage(String name)
    {
        try
        {
            BufferedImage img = ImageIO.read(BaseUtils.class.getResource(getTheme().themeDirectory() + name));
            Frame.report("Открыто локальное изображение: " + name);
            return img;
        } catch (IOException e)
        {
            Frame.reportErr("Ошибка при открытии изображения: " + name);
            return new BufferedImage(1, 1, 2);
        }
    }
    
    public static BufferedImage openImage(String path)
    {
        File file = new File(path);
        try
        {
            BufferedImage img = ImageIO.read(file.toURI().toURL());
            Frame.report("Открыто изображение: " + file.getName());
            return img;
        } catch (IOException e)
        {
            Frame.reportErr("Ошибка при открытии изображения: " + file.getName());
            return new BufferedImage(1, 1, 2);
        }
    }

    public String sendGET(String URL, Object[] params, boolean send)
    {
        HttpURLConnection ct = null;
        try
        {
            if (params.length % 2 != 0)
            {
                Frame.reportErr("GET запрос составлен некорректно, ожидалось четное число параметров");
                return null;
            }
            
            String params_line = "";
            for (int i = 0; i < params.length; i += 2)
            {
                String param_1 = "", param_2 = "";
                try { param_1 = params[i].toString(); } catch (Exception e) {}
                try { param_2 = params[i + 1].toString(); } catch (Exception e) {}
                
                params_line += param_1 + "=" + urlEncode(param_2);
                if (i + 2 < params.length) params_line += "&";
            }
            
            if (params.length != 0) params_line = "?" + params_line;
            if (send) Frame.report("Установка соединения с: " + URL + (isUseDebuging() ? params_line : ""));

            URL url = new URL(URL + params_line);
            ct = (HttpURLConnection) url.openConnection();
            ct.setRequestMethod("GET");
            ct.connect();

            InputStream is = ct.getInputStream();
            StringBuilder response;
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(is)))
            {
                response = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null)
                {
                    response.append(line);
                }
            }

            String str = response.toString();

            if (send) Frame.report("Соединение установлено. Получен ответ: '" + str + "'");
            return str;
        } catch (IOException e)
        {
            if (send) Frame.reportErr("Не удалось установить соединение с: " + URL + ", возвращаю null");
            return null;
        } finally
        {
            if (ct != null) ct.disconnect();
        }
    }
    
    public String sendPOST(String URL, Object[] params, boolean send)
    {
        HttpURLConnection ct = null;
        try
        {
            if (params.length % 2 != 0)
            {
                Frame.reportErr("POST запрос составлен некорректно, ожидалось четное число параметров");
                return null;
            }
            
            String params_line = "";
            for (int i = 0; i < params.length; i += 2)
            {
                String param_1 = "", param_2 = "";
                try { param_1 = params[i].toString(); } catch (Exception e) {}
                try { param_2 = params[i + 1].toString(); } catch (Exception e) {}
                
                params_line += param_1 + "=" + urlEncode(param_2);
                if (i + 2 < params.length) params_line += "&";
            }
            
            if (send) Frame.report("Установка соединения с: " + URL + (isUseDebuging() ? ", с параметрами: " + params_line : ""));

            URL url = new URL(URL);
            ct = (HttpURLConnection) url.openConnection();
            ct.setRequestMethod("POST");
            ct.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            ct.setRequestProperty("Content-Length", "0");
            ct.setRequestProperty("Content-Language", "en-US");
            ct.setUseCaches(false);
            ct.setDoInput(true);
            ct.setDoOutput(true);
            
            try (DataOutputStream wr = new DataOutputStream(ct.getOutputStream()))
            {
                wr.writeBytes(params_line);
                wr.flush();
            }

            InputStream is = ct.getInputStream();
            StringBuilder response;
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(is)))
            {
                response = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null)
                {
                    response.append(line);
                }
            }

            String str = response.toString();

            if (send) Frame.report("Соединение установлено. Получен ответ: '" + str + "'");
            return str;
        } catch (IOException e)
        {
            if (send) Frame.reportErr("Не удалось установить соединение с: " + URL + ", возвращаю null");
            return null;
        } finally
        {
            if (ct != null) ct.disconnect();
        }
    }
    
    public String uploadFile(String URL, Object[] params, boolean send)
    {
        try
        {
            if (params.length % 2 != 0)
            {
                Frame.reportErr("POST запрос составлен некорректно, ожидалось четное число параметров");
                return null;
            }
            
            if (send) Frame.report("Установка соединения с: " + URL + (isUseDebuging() ? ", с параметрами: " + Arrays.toString(params) : ""));
            
            String boundary = "----------------------" + BaseUtils.random(1000000, 9999999) + BaseUtils.random(1000000, 9999999) + BaseUtils.random(1000000, 9999999);
            
            URL url = new URL(URL);
            URLConnection ct;
            ct = url.openConnection();
            ct.setDoOutput(true);
            ct.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            if (params != null)
            {
                InputStream is = null;
                try (OutputStream os = ct.getOutputStream())
                {
                    for (int i = 0; i < params.length - 1; i += 2)
                    {
                        String name = params[i].toString();
                        Object object = params[i + 1];

                        if (object instanceof File)
                        {
                            File file = (File) object;
                            String filename = file.getPath();
                            is = new FileInputStream(file);

                            os.write("--".getBytes());
                            os.write(boundary.getBytes());
                            os.write("\r\n".getBytes());
                            os.write("Content-Disposition: form-data; name=\"".getBytes());
                            os.write(name.getBytes());
                            os.write('"');
                            os.write("; filename=\"".getBytes());
                            os.write(filename.getBytes());
                            os.write('"');
                            os.write("\r\n".getBytes());
                            os.write("Content-Type: ".getBytes());
                            String type = URLConnection.guessContentTypeFromName(filename);
                            if (type == null) type = "application/octet-stream";
                            os.write(type.getBytes());
                            os.write("\r\n".getBytes());
                            os.write("\r\n".getBytes());

                            byte[] buffer = new byte[65536];
                            int nread;
                            synchronized (is)
                            {
                                while ((nread = is.read(buffer, 0, buffer.length)) >= 0)
                                {
                                    os.write(buffer, 0, nread);
                                }
                            }
                            os.flush();
                            buffer = null;

                            os.write("\r\n".getBytes());
                        } else
                        {
                            os.write("--".getBytes());
                            os.write(boundary.getBytes());
                            os.write("\r\n".getBytes());
                            os.write("Content-Disposition: form-data; name=\"".getBytes());
                            os.write(name.getBytes());
                            os.write('"');
                            os.write("\r\n".getBytes());
                            os.write("\r\n".getBytes());
                            os.write(object.toString().getBytes());
                            os.write("\r\n".getBytes());
                        }
                    }
                    
                    os.write("--".getBytes());
                    os.write(boundary.getBytes());
                    os.write("--".getBytes());
                    
                    os.close();
                    if (is != null) is.close();
                }
                
                is = ct.getInputStream();
                StringBuilder response;
                try (BufferedReader rd = new BufferedReader(new InputStreamReader(is)))
                {
                    response = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null)
                    {
                        response.append(line);
                    }
                }

                String str = response.toString();
                
                if (send) Frame.report("Соединение установлено. Получен ответ: '" + str + "'");
                return str;
            } else
            {
                if (send) Frame.reportErr("Параметры не установлены, возвращаю null");
                return null;
            }
        } catch (IOException e)
        {
            if (send) Frame.reportErr("Не удалось установить соединение с: " + URL + ", возвращаю null");
            return null;
        }
    }
    
    public static String urlEncode(String url) throws IOException
    {
        return URLEncoder.encode(url, "UTF-8");
    }

    public static void sleep(double second)
    {
        try
        {
            Thread.sleep((long) (second * 1000));
        } catch (InterruptedException e) {}
    }

    public static UI_Theme getTheme()
    {
        return Settings.current_theme[0];
    }
    
    public static void setTheme()
    {
        String script = "jcr_theme.php?action=theme&version=" + Settings.version[0] + "&request=";
        
        try
        {
            if (useOnlineTheme())
            {
                Frame.report("Загужаю online тему...");
                
                String imgList[] = Frame.onlineData[1].split("<:i:>");
                for (int i = 0; i < imgList.length; i++)
                {
                    setThemeImages(loadImageIO(getURLSc(script), imgList[i]), i);
                    if (offlineTheme) break;
                }
                
                if (!offlineTheme) Frame.report("Online тема успешо загружена");
                else Frame.reportErr("Не удалось загрузить элементы online темы, запускаю тему по умолчанию");
            } else offlineTheme = true;
        } catch (Exception e)
        {
            offlineTheme = true;
            Frame.reportErr("Не удалось загрузить online тему, запускаю тему по умолчанию");
        }
        
        setThemeImages(null, -2);
        
        if (offlineTheme)
        {
            setThemeImages(null, -1);
            ThemeElements.initComponents();
            ThemeElements.disableThemeColor = Color.decode(getTheme().themeFieldsInactiveColor());
            ThemeElements.staticThemeColor = Color.decode(getTheme().themeFieldsStaticColor());
        } else
        {
            String colors = StyleUtils.getOnlineThemeColor();
            ThemeElements.initComponents();
            ThemeElements.disableThemeColor = Color.decode(colors.split(":s:")[0]);
            ThemeElements.staticThemeColor = Color.decode(colors.split(":s:")[1]);
        }
    }
    
    public static BufferedImage loadImageIO(String url, String name) throws Exception
    {
        BufferedImage img;
        try
        {
            img = ImageIO.read(new URL(url + name));
            Frame.report(" * Загружено изображение: " + name); return img;
        } catch (IOException e)
        {
            Frame.reportErr(" * Загрузка прервана на элементе: " + name);
            offlineTheme = true;
            return null;
        }
    }
    
    public static void setThemeImages(BufferedImage img, int num)
    {
        if (img == null && num == -1)
        {
            ThemeElements.favicon		= openLocalImage(getTheme().themeFavicon());
            ThemeElements.background		= openLocalImage(getTheme().themeBackground());
            ThemeElements.logotype		= openLocalImage(getTheme().themeLogotype());
            ThemeElements.authFields		= openLocalImage(getTheme().themeAuthFields());
            ThemeElements.sysButs		= openLocalImage(getTheme().themeSysButs());
            ThemeElements.button		= openLocalImage(getTheme().themeButton());
            ThemeElements.combobox		= openLocalImage(getTheme().themeComboBox());
            ThemeElements.checkbox		= openLocalImage(getTheme().themeCheckBox());
            ThemeElements.fieldBack		= openLocalImage(getTheme().themeFieldBack());
            ThemeElements.progBarImage		= openLocalImage(getTheme().themeProgressBar());
            ThemeElements.modalBack		= openLocalImage(getTheme().themeModalBack());
            ThemeElements.news_back		= openLocalImage(getTheme().themeNewsBack());
            ThemeElements.pressBorder		= openLocalImage(getTheme().themePressedBorder());
            ThemeElements.waitIcon		= openLocalImage(getTheme().themeWaitIcon());
            ThemeElements.alertIcons		= openLocalImage(getTheme().themeAlertIcons());
            ThemeElements.bandColors		= openLocalImage(getTheme().themeBandColors());
            ThemeElements.personal_alert	= openLocalImage(getTheme().themePersonalAlert());
            ThemeElements.def_skin		= openLocalImage("char.png");
        } else if (img == null && num == -2)
        {
            ThemeElements.def_skin		= openLocalImage("char.png");
        } else
        {
            switch (num)
            {
                case 0: ThemeElements.favicon = img;
                case 1: ThemeElements.background = img;
                case 2: ThemeElements.logotype = img;
                case 3: ThemeElements.authFields = img;
                case 4: ThemeElements.sysButs = img;
                case 5: ThemeElements.button = img;
                case 6: ThemeElements.combobox = img;
                case 7: ThemeElements.checkbox = img;
                case 8: ThemeElements.fieldBack = img;
                case 9: ThemeElements.progBarImage = img;
                case 10: ThemeElements.modalBack = img;
                case 11: ThemeElements.news_back = img;
                case 12: ThemeElements.pressBorder = img;
                case 13: ThemeElements.waitIcon = img;
                case 14: ThemeElements.alertIcons = img;
                case 15: ThemeElements.bandColors = img;
                case 16: ThemeElements.personal_alert = img;
            }
        }
    }
    
    public static String buildURL(String domain, String path)
    {
        return "http://" + domain + "/" + Settings.site_dir[0] + "/" + path;
    }
    
    public static String getURL(String path)
    {
        return "http://" + Settings.domain[0] + "/" + Settings.site_dir[0] + "/" + path;
    }
    
    public static String getURLSc(String script)
    {
        return getURL("scripts/" + script);
    }
    
    public static String getURLFi(String folder)
    {
        return getURL("files/" + folder);
    }
    
    public String[] loadOnlineSettings(SplashFrame sframe)
    {
        Frame.report("Загрузка online настроек...");
        if (sframe!= null) sframe.setStatus("загрузка online настроек...");
        
        try
        {
            String url = sendGET(
                    getURLSc("jcr_theme.php")
                    , new Object[]
                    {
                        "action", "settings",
                        "request", "elements",
                        "version", Settings.version[0]
                    }
                    , false
            );
            
            if (url == null)
            {
                Frame.reportErr("Не удалось загрузить online настройки");
                return null;
            } else if (url.contains("<::>"))
            {
                return url.replaceAll("<br>", "").split("<::>");
            } else
            {
                Frame.reportErr("Не удалось загрузить online настройки");
                return null;
            }
        } catch (Exception e)
        {
            Frame.reportErr("Не удалось загрузить online настройки");
            return null;
        }
    }
    
    /* Получение размера предстоящего обновления */
    public int getSizeOfUpdate(List<String> files)
    {
        Frame.report("Получения суммарного размера загружаемых файлов...");
        try
        {
            String updateFiles = "";
            for (int i = 0; i < files.size(); i++)
            {
                updateFiles += files.get(i);
                if (i + 1 < files.size()) updateFiles += "<:f:>";
            }
            
            String url = sendPOST(
                    getURLSc("jcr_theme.php")
                    , new Object[]
                    {
                        "action", "updateSize",
                        "client", getClientName(),
                        "updateFiles", updateFiles
                    }
                    , false
            );
            
            if (url == null)
            {
                Frame.reportErr("Не удалось получить суммарный размер обновления");
                return 1;
            } else if (url.contains("=:="))
            {
                int size = new Integer(url.replace("=:=", ""));
                return size != 0 ? size : 1;
            } else
            {
                Frame.reportErr("Не удалось получить суммарный размер обновления");
                return 1;
            }
        } catch (Exception e)
        {
            Frame.reportErr("Не удалось получить суммарный размер обновления: " + e.getCause());
            return 1;
        }
    }
    
    public static boolean useOnlineTheme()
    {
        try
        {
            return Frame.onlineData[0].equals("true");
        } catch (Exception e) { return false; }
    }
    
    public static String[] getServersNames()
    {
        String[] error = { "Ошибка подключения", "error" };
        try
        {
            String url = baseUtils.sendGET(
                    getURLSc("jcr_status.php")
                    , new Object[] { "action", "servers" }
                    , false
            );

            if (url == null || Frame.onlineData == null)
            {
                Frame.reportErr("Не удалось загрузить список серверов");
                return error;
            } else if (url.contains(" :: "))
            {
                servers = url.replaceAll("<br>", "").split("<::>");
                String[] serversNames = new String[servers.length];

                for (int a = 0; a < servers.length; a++)
                {
                    serversNames[a] = servers[a].split(" :: ")[0];
                }

                return serversNames;
            } else
            {
                Frame.reportErr("Не удалось загрузить список серверов");
                return error;
            }
        } catch (Exception e)
        {
            Frame.reportErr("Не удалось загрузить список серверов");
            return error;
        }
    }
    
    public static String getServerOnline(String[] server)
    {
        return baseUtils.sendGET(
                getURLSc("jcr_status.php")
                , new Object[]
                {
                    "action", "status",
                    "ip", server[1],
                    "port", server[2]
                }
                , false
        );
    }
    
    public static String[] getServerAbout()
    {
        int index = getPropertyInt("server") + 1;
        
        if (servers != null)
            return servers[index].split(" :: ");
        return null;
    }
    
    public static String getClientName()
    {
        if (Frame.frame.isOffline())
            return ThemeUtils.offlineGameUtils.getClientName(Frame.frame.serversList.getSelectedIndex());
        
        String[] server_info = getServerAbout();
        if(server_info != null) return server_info[0];
        
        return null;
    }
    
    public static String getClientVersion()
    {
        if (Frame.frame.isOffline())
        {
            return ThemeUtils.offlineGameUtils.getClientVersion(Frame.frame.serversList.getSelectedIndex());
        }
        
        String[] server_info = getServerAbout();
        if(server_info != null)
            return server_info[3];
        
        return null;
    }
    
    public static String getParentDirectory()
    {
        String dir;
        if (getPlatform() != 0)
            dir = getGameDirectory(Settings.game_directory[0]).toString();
        else
            dir = getGameDirectory(Settings.game_directory[0].replaceFirst(".", "")).toString();
        
        return dir;
    }
    
    public static String getClientFolder()
    {
        if(Settings.use_multi_client[0])
            return getClientName();
        return "main_client";
    }
    
    public static String getClientDirectory()
    {
        if (Frame.frame.isOffline())
            return ThemeUtils.offlineGameUtils.getClientPath(Frame.frame.serversList.getSelectedIndex());
        
        String dir;
        if (getPlatform() != 0)
            dir = getGameDirectory(Settings.game_directory[0]).toString();
        else
            dir = getGameDirectory(Settings.game_directory[0].replaceFirst(".", "")).toString();
        
        return dir + File.separator + getClientFolder();
    }
    
    public static File getGameDirectory(String gameDirectory)
    {
        String home = System.getProperty("user.home", ".");
        File fiDir;
        switch (getPlatform())
        {
            case 0:
            case 1:
                fiDir = new File(home, gameDirectory + File.separator);
                break;
            case 2:
                String appData = System.getenv(Settings.par_directory[0]);
                if (appData != null)
                    fiDir = new File(appData, gameDirectory + File.separator);
                else
                    fiDir = new File(home, gameDirectory + File.separator);
                break;
            case 3:
                fiDir = new File(home, "Library/Application Support/" + gameDirectory + File.separator);
                break;
            default:
                fiDir = new File(home, gameDirectory + File.separator);
        }
        if (!fiDir.exists() && !fiDir.mkdirs())
        {
            Frame.reportErr("Директория не найдена: " + fiDir);
        }
        return fiDir;
    }
    
    public static int getPlatform()
    {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win"))
            return 2;
        if (osName.contains("mac"))
            return 3;
        if (osName.contains("solaris"))
            return 1;
        if (osName.contains("sunos"))
            return 1;
        if (osName.contains("linux"))
            return 0;
        if (osName.contains("unix"))
            return 0;

        return 4;
    }
    
    static { config.load(); }
    
    public static void setProperty(String s, Object value)
    {
        if (config.checkProperty(s))
            config.changeProperty(s, value);
        else config.put(s, value);
    }
    
    public static void deleteProperty(String s)
    {
        if (config.checkProperty(s))
            config.deleteProperty(s);
    }
    
    public static String getPropertyString(String s)
    {
        if (config.checkProperty(s))
            return config.getPropertyString(s);
        return null;
    }

    public static boolean getPropertyBoolean(String s)
    {
        if (config.checkProperty(s))
            return config.getPropertyBoolean(s);
        return false;
    }

    public static int getPropertyInt(String s)
    {
        if (config.checkProperty(s))
            return config.getPropertyInteger(s);
        return 0;
    }
    
    public static void writeConfig()
    {
        Frame frame = Frame.frame;
        
        if (frame.set_remember.isSelected())
        {
            setProperty("login", EncodingUtils.encode(Frame.authData[23]));
            if (Settings.use_pass_remember[0])
                setProperty("password", EncodingUtils.encode(new String(frame.password.getPassword())));
        } else
        {
            deleteProperty("login");
            if (Settings.use_pass_remember[0])
                deleteProperty("password");
        }
        
        int remPr;
        if (frame.set_remember.isSelected()) remPr = 1; else remPr = 2;
        setProperty("remember", remPr);
        
        setProperty("full_screen", frame.set_full.isSelected());
    }
    
    public static void readConfig(final Frame frame)
    {
        String loginPr = EncodingUtils.decode(getPropertyString("login"));
        if (!loginPr.equals("")) frame.login.setText(loginPr);
        
        if (Settings.use_pass_remember[0])
        {
            String passPr = EncodingUtils.decode(getPropertyString("password"));
            if (!passPr.isEmpty()) frame.password.setText(passPr);
        }
        
        int remPr = getPropertyInt("remember");
        if (remPr == 0 || remPr == 1) frame.set_remember.setSelected(true);
        else frame.set_remember.setSelected(false);
        
        frame.set_full.setSelected(getPropertyBoolean("full_screen"));
        
        int memory = getPropertyInt("memory");
        if (memory >= 256) frame.set_memory.setText(memory+"");
    }
    
    // Возвращает [n] последних символов из строки
    public static String lastNchars(String str, int num)
    {
        return str.substring(str.length() - num, str.length());
    }
    
    public static void definitionFrames(Frame frame)
    {
        if (Settings.use_personal[0])
        {
            frame.panel.waitIcon(true, 391, 0);
            PersonalCab.setImages(Frame.authData, frame);
            frame.panel.waitIcon(false, 391, 0);
            
            frame.toXFrame(5);
        } else
        {
            prepareUpdate();
        }
    }
    
    public static void prepareUpdate()
    {
        if (Frame.frame.set_update.isSelected())
        {
            deleteProperty(getServerAbout()[1] + "_" + getServerAbout()[2] + "_hashZip");
            deleteProperty(getServerAbout()[1] + "_" + getServerAbout()[2] + "_hashNat");
            deleteProperty(getServerAbout()[1] + "_" + getServerAbout()[2] + "_hashAss");
            delete(new File(getClientDirectory()));
            Frame.frame.set_update.setSelected(false);
        }
        
        boolean zipupdate = false;
        boolean natupdate = false;
        boolean assupdate = false;
        String[] data = Frame.authData;
        String[] mods = data[9].split("<:f:>");
        String[] coremods = data[13].split("<:f:>");
        String[] configs = data[22].split("<:f:>");
        List<String> files = new ArrayList<>();
        
        Frame.report("Проверка директорий перед обновлением...");
        List<Boolean> list = new ArrayList<>(); list.add(true);
        GuardUtils.removeEmptyFolders(getClientDirectory() + File.separator + "mods", list, true);
        GuardUtils.checkDir(getClientDirectory() + File.separator + "mods", data[9], true);
        
        if (versionCompare(getClientVersion(), "1.6") == 2)
        {
            list.clear(); list.add(true);
            GuardUtils.removeEmptyFolders(getClientDirectory() + File.separator + "coremods", list, true);
            GuardUtils.checkDir(getClientDirectory() + File.separator + "coremods", data[13], true);
        }
        Frame.report("Проверка директорий перед обновлением завершена");
        
        String binfolder = getClientDirectory() + File.separator + "bin" + File.separator;
        String modsfolder = getClientDirectory() + File.separator + "mods" + File.separator;
        String coremodsfolder = getClientDirectory() + File.separator + "coremods" + File.separator;
        String configfolder = getClientDirectory() + File.separator + "config" + File.separator;
        
        if (!data[0].equalsIgnoreCase(getPropertyString(getServerAbout()[1] + "_" + getServerAbout()[2] + "_hashZip"))) { files.add("extra.zip"); zipupdate = true; }
        if (!data[1].equalsIgnoreCase(GuardUtils.md5_file(binfolder + "minecraft.jar"))) files.add("bin/minecraft.jar");
        if (!data[15].equalsIgnoreCase(getPropertyString(getServerAbout()[1] + "_" + getServerAbout()[2] + "_hashNat")) || !new File(binfolder, "natives").exists()) { files.add("bin/natives.zip"); natupdate = true; }
        if (versionCompare(getClientVersion(), "1.6") == 2)
        {
            if (!data[2].equalsIgnoreCase(GuardUtils.md5_file(binfolder + "lwjgl.jar"))) files.add("bin/lwjgl.jar");
            if (!data[3].equalsIgnoreCase(GuardUtils.md5_file(binfolder + "lwjgl_util.jar"))) files.add("bin/lwjgl_util.jar");
            if (!data[4].equalsIgnoreCase(GuardUtils.md5_file(binfolder + "jinput.jar"))) files.add("bin/jinput.jar");
        } else
        {
            if (!data[17].equalsIgnoreCase(GuardUtils.md5_file(binfolder + "libraries.jar"))) files.add("bin/libraries.jar");
            if (!new File(getClientDirectory() + "/assets").exists() || !data[16].equalsIgnoreCase(getPropertyString(BaseUtils.getServerAbout()[1] + "_" + BaseUtils.getServerAbout()[2] + "_hashAss"))) { delete(new File(getClientDirectory() + "/assets")); files.add("assets.zip"); assupdate = true; }
            if (getServerAbout()[4].equalsIgnoreCase("true") && !data[18].equals(GuardUtils.sha1(GuardUtils.md5_file(binfolder + "forge.jar")))) files.add("bin/forge.jar");
            if (getServerAbout()[5].equalsIgnoreCase("true") && !data[19].equals(GuardUtils.sha1(GuardUtils.md5_file(binfolder + "liteloader.jar")))) files.add("bin/liteloader.jar");
        }
        
        if (!mods[0].equals("nomods"))
        for (String mod : mods)
        {
            String[] mHash = mod.split("<:h:>");
            if (!mHash[1].equals(GuardUtils.md5_file(modsfolder + mHash[0]))) files.add("mods/" + mHash[0]);
        }
        
        if (versionCompare(getClientVersion(), "1.6") == 2)
        {
            if (!coremods[0].equals("nocoremods"))
            for (String coremod : coremods)
            {
                String[] mHash = coremod.split("<:h:>");
                if (!mHash[1].equals(GuardUtils.md5_file(coremodsfolder + mHash[0]))) files.add("coremods/" + mHash[0]);
            }
        }
        
        if (!configs[0].equals("noconfigs"))
        {
            for (String confdata : configs)
            {
                String[] conf_hash = confdata.split("<:h:>");
                if (!GuardUtils.md5_file(configfolder + conf_hash[0]).equalsIgnoreCase(conf_hash[1])) files.add("config/" + conf_hash[0]);
            }
        }
        
        if (!data[24].equals("nocheckfs"))
        files.addAll(GuardUtils.checkSelectedFiles(data[24]));
        
        if (!files.isEmpty())
        {
            Frame.report("Список загружаемых файлов: ");
            for (Object s : files.toArray()) Frame.report(" * " + s.toString());
        }
        
        if (BaseUtils.getPlatform() == 0)
            Frame.frame.setSize(Settings.frame_width[0] + Settings.linux_frame_w[0], Settings.frame_height[0] + Settings.linux_frame_h[0]); 
        
        gameUpdater = new GameUpdater(files, zipupdate, natupdate, assupdate, data);
        
        Frame.frame.toXFrame(4);
        
        gameUpdater.start();
    }
    
    public static void startGame(String[] data)
    {
        if (!Frame.frame.isOffline())
            setProperty(getClientFolder(), getServerAbout()[0] + "-::-" + getClientVersion() + "-::-" + getServerAbout()[4] + "-::-" + getServerAbout()[5]);
        
        if (versionCompare(getClientVersion(), "1.6") == 1)
        {
            try { new GameLauncher(data); }
            catch (IOException e) { Frame.reportErr("Ошибка при запуске игры"); e.printStackTrace(); }
        } else
        {
            new Game(data);
        }
    }
    
    /**
     * Вернет 1, если первая версия игры будет больше второй;
     * Вернет 2, если вторая версия будет больше первой;
     * Вернет 0, если возникнет ошибка.
     */
    public static int versionCompare(String one, String two)
    {
        try
        {
            String one_s = "", two_s = "";
            
            for (int i = 0; i < one.length(); i++)
            {
                try { one_s += Integer.parseInt(one.substring(i, i + 1)); }
                catch (NumberFormatException e) {}
            }
            
            for (int i = 0; i < two.length(); i++)
            {
                try { two_s += Integer.parseInt(two.substring(i, i + 1)); }
                catch (NumberFormatException e) {}
            }
            
            if (one_s.length() > two_s.length()) one_s = one_s.substring(0, two_s.length());
            else if (one_s.length() < two_s.length()) two_s = two_s.substring(0, one_s.length());
            
            int one_num = Integer.parseInt(one_s);
            int two_num = Integer.parseInt(two_s);
            
            if (one_num >= two_num) return 1;
            else return 2;
            
        } catch(NumberFormatException e)
        {
            e.printStackTrace();
            return 0;
        }
    }
    
//    public static boolean checkInternetConnection()
//    {
//        try
//        {
//            return checkInternetConnection(new URL("http://" + Settings.domain));
//        } catch (MalformedURLException ex)
//        {
//            return false;
//        }
//    }
    
//    public static boolean checkInternetConnection(URL url)
//    {
//        boolean result = false;
//        HttpURLConnection con = null;
//        try
//        {
//            con = (HttpURLConnection) url.openConnection();
//            con.setRequestMethod("HEAD");
//            result = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
//        } catch (IOException e) {}
//        finally
//        {
//            if (con != null)
//            {
//                try { con.disconnect(); }
//                catch (Exception e) { e.printStackTrace(); }
//            }
//        }
//        return result;
//    }
    
    public static void decisionToSendReport(boolean create_new_stream)
    {
        if (GuardUtils.use_send_report && !GuardUtils.all_clean)
        {
            if (create_new_stream) new Thread() { public void run() { sendReport((String[]) GuardUtils.find_files.toArray(new String[] {}), ""); }}.start();
            else sendReport((String[]) GuardUtils.find_files.toArray(new String[] {}), "");
        }
    }
    
    /**
     * Отправляет оповещание администратору на веб-сервер, если обнаружены сторонние файлы
     * @param find_files // массив с директориями найденных файлов
     * @param note // заметка
     */
    private static void sendReport(String[] find_files, String note)
    {
        Frame.report("GUARD: Отправка отчета...");
        
        if (find_files.length == 0) { Frame.report("GUARD: Отчет не был отправлен, так как список файлов пуст"); return; }
        
        String files = "";
        for (String find_file : find_files) files += find_file + "<:f:>";
        files = files.substring(0, files.length() - 5).replaceAll(" ", "_");
        
        try
        {
            String answer = baseUtils.sendPOST(
                    getURLSc("jcr_auth.php")
                    , new Object[]
                    {
                        "action", "report",
                        "login", Frame.frame.login.getText(),
                        "password", new String(Frame.frame.password.getPassword()),
                        "files", files,
                        "message", note,
                        "session", Frame.authData[7],
                        "code", GuardUtils.sha1(Settings.protect_key[0])
                    }
                    , false
            );
            
            if (answer.equalsIgnoreCase("Done"))
            {
                Frame.report("GUARD: Отчет успешно отправлен");
            } else
            {
                Frame.reportErr("GUARD: Не удалось отправить отчет");
            }
        } catch (Exception e)
        {
            Frame.reportErr("GUARD: Не удалось отправить отчет");
        }
    }
    
    public static void openLink(String url)
    {
        try
        {
            Object o = Class.forName("java.awt.Desktop").getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
            o.getClass().getMethod("browse", new Class[] { URI.class }).invoke(o, new Object[] { new URI(url) });
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | URISyntaxException e)
        { Frame.reportErr("Не удалось открыть ссылку: " + url); }
    }
    
    public static void delete(File file)
    {
        try {
            if (!file.exists()) return;
            if (file.isDirectory())
            {
                for (File f : file.listFiles()) delete(f);
                file.delete();
            } else file.delete();
        } catch (Exception e)
        { Frame.reportErr("Удаление не удалось: " + file.toString()); }
    }
    
    public static void patchClient(eURLClassLoader cl)
    {
        if (!Settings.path_client[0] || versionCompare(getClientVersion(), "1.6") == 1) return;
        
        try
        {
            String mcver = Frame.frame.isOffline() ? getClientVersion() : getServerAbout()[3];
            String[] library = Settings.libraryForPath;
            
            Frame.report("Запуск процесса патчинга: ");
            Frame.report(" * Обнаружение клиента...");
            Frame.report(" * Клиент: " + getClientName() + " :: " + mcver);
            Frame.report(" * Поиск версии в библиотеке...");
            
            for (String lib : library)
            {
                if (mcver.contains(lib.split("::")[0].replace("x", "")))
                {
                    Frame.report(" * Патчинг клиента...");
                    Field f = cl.loadClass(Settings.old_mine_class[0]).getDeclaredField(lib.split("::")[1]);
                    Field.setAccessible(new Field[] { f }, true);
                    f.set(null, new File(getClientDirectory()));
                    Frame.report(" * Файл пропатчен: " + Settings.old_mine_class[0] + " :: " + lib.split("::")[1]);
                    Frame.report(" * Патчинг клиента успешно завершен");
                    return;
                }
            }
            
            Frame.reportErr(" * Данная версия клиента не обнаружена!");
            Frame.reportErr(" * Не удалось произвести патчинг клиента");
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
        {
            Frame.reportErr(" * Ошибка: поле клиента не корректно");
        }
    }
    
    /* В данный момент функция позволяет ускорить загрузку клиента */
    public static void patchNewClientUrls(eURLClassLoader cl)
    {
        if (versionCompare(getClientVersion(), "1.7") == 2) return;
        
        try
        {
            String p_class = "com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService";
            Object[][] data = new Object[][]
            {
                { "BASE_URL", getURLSc("") },
                { "JOIN_URL", new URL(getURLSc("jcr_joinserver.php")) },
                { "CHECK_URL", new URL(getURLSc("jcr_hasjoined.php")) }
            };
            
            Frame.report("Запуск процесса патчинга клиента в целях ускорения его загрузки...");
//            Frame.report("Запуск процесса патчинга клиента: ");
//            Frame.report(" * Обнаружение клиента...");
//            Frame.report(" * Клиент: " + getClientName());
//            Frame.report(" * Патчинг клиента...");
            
            Class c = cl.loadClass(p_class);
            
            for (Object[] cur : data)
            {
//                Frame.report(" * Патчинг поля: " + cur[0]);
                Field f = c.getDeclaredField((String) cur[0]);
                Field.setAccessible(new Field[] { f }, true);
                
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                
                f.set(null, cur[1]);
            }
            
//            Frame.report(" * Файл пропатчен: " + p_class);
//            Frame.report(" * Патчинг клиента успешно завершен");
            
        } catch (Exception e)
        {
            Frame.reportErr(" * Не удалось произвести патчинг URL: " + e.getCause());
        }
    }
    
    public static void updateProgram() throws Exception
    {
        Frame.report("Запуск процесса обновления программы...");
        
        String appURL = getURLFi("program/" + Frame.authData[14]);
        Frame.report("Загрузка файла: " + appURL);
        
        InputStream is = new BufferedInputStream(new URL(appURL).openStream());
        FileOutputStream fos = new FileOutputStream(GuardUtils.appPath());

        int bs = 0;
        byte[] buffer = new byte[65536];
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        while ((bs = is.read(buffer, 0, buffer.length)) != -1)
        {
            fos.write(buffer, 0, bs);
            md5.update(buffer, 0, bs);
        }
        is.close();
        fos.close();
        Frame.report("Файл загружен: " + appURL);
        
        if (getPlatform() == 3)
        {
            Frame.report("Обновление завершено успешно");
            Frame.frame.panel.waitIcon(false, 1);
            Frame.frame.skip.setEnabled(false);
            Frame.frame.take_self_renewal.setEnabled(false);
            Frame.frame.setAlert("Требуется перезапуск", 1, 391, 1);
            Frame.report("Требуется перезапуск программы");
        } else
        {
            Starter.launchProgramAndExit();
        }
    }
    
    public static String getProgramFormat()
    {
        String[] format = { ".jar", ".exe" };
        String path = GuardUtils.appPath().toLowerCase();
        if (path.substring(path.lastIndexOf("/")).contains(format[1]))
            return format[1];
        else
            return format[0];
    }
    
    public static void restart()
    {
        Frame frame = Frame.frame;
        
        if (frame.isOffline())
        {
            if (!ThemeUtils.offlineGameUtils.noClients())
            {
                int index = frame.serversList.getSelectedIndex();
                if (index != 0)
                    setProperty("server_name", ThemeUtils.offlineGameUtils.getClientName(index));
                setProperty("offline_server", index);
            }
            
            setProperty("offline_mode", true);
        }
        
        Frame.report("Перезапуск программы...");
        try { Starter.launchProgramAndContinue(frame); }
        catch (Exception e) { e.printStackTrace(); }
    }
    
    public static int findString(String[] array, String str)
    {
        for (int a = 0; a < array.length; a++)
            for (int b = 0; b < array.length; b++)
                if (array[a] != null && array[a].equals(str)) return a;
        
        return -1;
    }
    
    public static long random(long from, long to)
    {
        Random random = new Random();
        return from + Math.abs(random.nextLong()) % (to - (from - 1));
    }
    
    public static String getDebugKey()
    {
        return GuardUtils.sha1(GuardUtils.md5(Settings.debugKey[0]));
    }
    
    public static boolean isUseDebuging()
    {
        try { return Frame.onlineData[4].equalsIgnoreCase(getDebugKey()); }
        catch (Exception e) { return false; }
    }
}
