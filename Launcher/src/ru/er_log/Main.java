package ru.er_log;

import ru.er_log.components.Frame;
import ru.er_log.splash.state.SplashScreen;
import ru.er_log.utils.BaseUtils;

public class Main {

    public static void main(String[] args) throws Exception
    {
        if (!BaseUtils.getPropertyBoolean("program_started"))
        {
            //Frame.reportErr("Пожалуйста, используйте в качестве главного класса '" + Starter.class.getCanonicalName() + "'");
            //return;
        }
        
        BaseUtils.setProperty("program_started", false);
        SplashScreen.start();
    }
}
