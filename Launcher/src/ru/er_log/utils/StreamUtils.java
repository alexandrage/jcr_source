package ru.er_log.utils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import ru.er_log.Settings;
import ru.er_log.components.Frame;
import static ru.er_log.components.Frame.*;
import ru.er_log.components.PersonalCab;
import ru.er_log.components.ThemeElements;
import static ru.er_log.utils.BaseUtils.*;

public class StreamUtils {
    
    public static Thread t = null;
    
    public static void doLogin()
    {
        join(t);
        t = new Thread() {
            public void run()
            {
                report("Авторизация...");
                frame.elEnabled(false);
                frame.panel.waitIcon(true, 0);

                String auth = null;
                
                try
                {
                    auth = baseUtils.sendPOST(
                            getURLSc("jcr_auth.php"),
                            new Object[]
                            {
                                "action", "auth",
                                "login", frame.login.getText(),
                                "password", new String(frame.password.getPassword()),
                                "hash", GuardUtils.md5_file(GuardUtils.appPath()),
                                "format", getProgramFormat(),
                                "client", getClientName(),
                                "version", getClientVersion(),
                                "forge", getServerAbout()[4],
                                "liteloader", getServerAbout()[5],
                                "mac", GuardUtils.getHWID(),
                                "code", GuardUtils.sha1(Settings.protect_key[0])
                            },
                            true
                    );
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Frame.reportErr("Не удалось пройти авторизацию");
                }
                
                frame.panel.resetAlerts();
                
                if (auth == null)
                {
                    frame.setAlert("Ошибка подключения", 2, 0);
                } else if (auth.trim().equals("BadParams") || auth.toLowerCase().contains("error"))
                {
                    frame.setAlert("Внутренняя ошибка", 2, 0);
                    reportErr("Ошибка в переданных параметрах");
                } else if (auth.trim().equals("BadCode"))
                {
                    frame.setAlert("Внутренняя ошибка", 2, 0);
                    reportErr("Неверный код доступа на web-сервер");
                } else if (auth.trim().equals("BadHWID"))
                {
                    frame.setAlert("Ошибка доступа", 3, 0);
                    reportErr("Неверный HWID пользователя");
                } else if (auth.trim().equals("BadUserHWID"))
                {
                    frame.setAlert("Войдите со своего компьюетра", 3, 0);
                    reportErr("HWID пользователя не совпал с оригинальным");
                } else if (auth.trim().equals("Banned"))
                {
                    frame.setAlert("Вы заблокированы", 2, 0);
                    reportErr("Пользователь заблокирован");
                } else if (auth.contains("<::>"))
                {
                    authData = auth.replaceAll("<br>", "").split("<::>");

                    BaseUtils.writeConfig();
                    
                    frame.setAlert("Вход выполнен успешно", 1, 0);
                    BaseUtils.sleep(1.0);
                    
                    if (authData[21].equals("true")) frame.toXFrame(6);
                    else if (authData[8].equals("true") || BaseUtils.isUseDebuging())
                    {
                        definitionFrames(frame);
                    } else
                    {
                        BaseUtils.sleep(1.0);
                        frame.toXFrame(3);
                    }
                    
                    frame.panel.repaint();
                } else
                {
                    frame.setAlert("Неверный логин или пароль", 3, 0);
                }
                
                frame.elEnabled(true);
            }
        }; t.setName("doLogin"); t.start();
    }
    
    public static void uploadFile(final File file, final int type, final Frame frame)
    {
        join(t);
        t = new Thread() {
            public void run()
            {
                frame.panel.resetAlerts();
                frame.panel.waitIcon(true, 391, 4);
                String auth = null;
                
                try
                {
                    auth = baseUtils.uploadFile(
                            getURLSc("jcr_auth.php")
                            , new Object[]
                            {
                                "action", type > 0 ? "upload_cloak" : "upload_skin",
                                "login", frame.login.getText(),
                                "password", new String(frame.password.getPassword()),
                                "code", GuardUtils.sha1(Settings.protect_key[0]),
                                "userfile", file
                            }
                            , false
                    );
                } catch (Exception e)
                {
                    Frame.reportErr("Не удалось загрузить файл");
                }
                
                frame.panel.waitIcon(false, 391, 4);
                
                if (auth == null)
                {
                    frame.setAlert("Ошибка подключения", 2, 400, 4);
                } else if (auth.contains("error"))
                {
                    frame.setAlert("Ошибка при загрузке", 2, 400, 4);
                    Frame.report("Ошибка при загрузке файла");
                } else if (auth.equalsIgnoreCase("noaccess"))
                {
                    frame.setAlert("Недостаточно прав для загрузки", 2, 400, 4);
                    Frame.report("Недостаточно прав для загрузки файла");
                } else if (auth.equalsIgnoreCase("success"))
                {
                    frame.setAlert("Файл успешно загружен", 1, 400, 4);
                    Frame.report("Файл успешно загружен: " + file.getAbsolutePath());
                    PersonalCab.refreshImage(file.getAbsolutePath(), type, frame);
                } 
            }
        }; t.setName("uploadFile"); t.start();
    }
    
    public static void loadNewsPage(final String url)
    {
        Frame.report("Загрузка новостей...");
        final Color c = ThemeElements.staticThemeColor;
        join(t);
        t = new Thread() {
            public void run()
            {
                try
                {
                    Frame.frame.news_pane.setPage(url);
                    Frame.report("Страница новостей загружена");
                } catch (IOException e)
                {
                    Frame.frame.news_pane.setText("<center><font color=\"rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")\" style=\"font-family: Arial, Tahoma, Helvetica, sans-serif\">Не удалось загрузить новости</font></center>");
                    Frame.reportErr("Ошибка при загрузке новостей");
                }
            }
        }; t.setName("loadNewsPage"); t.start();
    }
    
    public static void getServerOnline()
    {
        if (t != null && t.getName().equals("getServerOnline")) { t.interrupt(); t.stop(); } else join(t);
        t = new Thread() {
            public void run()
            {
                try
                {
                    String server[] = getServerAbout();
                    String url = BaseUtils.getServerOnline(server);

                    frame.panel.resetAlerts();

                    if (url == null)
                    {
                        reportErr("Ошибка подключения к серверу: " + url);
                        frame.setAlert("Ошибка подключения", 2, 0);
                        frame.panel.waitIcon(false, 0);
                    } else if (url.contains("<::>"))
                    {
                        String[] result = url.split("<::>");
                        if (new Integer(result[0]) >= new Integer(result[1]))
                        {
                            frame.setAlert("Сервер полон: " + result[0] + " из " + result[1], 3, 0);
                        } else
                        {
                            frame.setAlert("Сервер доступен: " + result[0] + " из " + result[1], 1, 0);
                        }
                        frame.panel.waitIcon(false, 0);
                    } else if (url.trim().equals("OFF"))
                    {
                        frame.setAlert("Сервер недоступен", 2, 0);
                        frame.panel.waitIcon(false, 0);
                    } else if (url.trim().equals("TechWork"))
                    {
                        frame.setAlert("Технические работы", 3, 0);
                        frame.panel.waitIcon(false, 0);
                    } else
                    {
                        reportErr("Внутренняя ошибка: " + url);
                        frame.setAlert("Внутренняя ошибка", 2, 0);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }; t.setName("getServerOnline"); t.start();
    }

    public static void actionTakeUpdate()
    {
        join(t);
        t = new Thread() {
            public void run()
            {
                try
                {
                    frame.take_self_renewal.setEnabled(false);
                    frame.panel.waitIcon(true, 382, 1);
                    frame.setAlert("", 0, 1);
                    BaseUtils.sleep(1.0);
                    updateProgram();
                } catch (Exception e)
                {
                    e.printStackTrace();
                    
                    frame.setAlert("Ошибка при обновлении", 3, 391, 1);
                    reportErr("Ошибка при обновлении программы");
                    
                    frame.take_self_renewal.setEnabled(true);
                    frame.take_self_renewal.setText("Еще раз");
                    
                    frame.panel.waitIcon(false, 1);
                }
            }
        }; t.setName("actionTakeUpdate"); t.start();
    }
    
    public static void join(Thread t)
    {
        if (t != null)
        {
            try { t.join(); }
            catch (InterruptedException ex)
            { Frame.report("Не удалось дождаться завершения потока: " + t.getName()); }
        }
    }
}
