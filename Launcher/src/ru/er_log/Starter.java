package ru.er_log;

import java.io.File;
import java.util.ArrayList;
import javax.swing.JFrame;
import ru.er_log.components.Frame;
import ru.er_log.splash.chooser.ChooserScreen;
import ru.er_log.utils.BaseUtils;
import ru.er_log.utils.GuardUtils;
import ru.er_log.utils.ProcessUtils;

public class Starter {
    
    private static boolean program_is_already_started = false;
    private static boolean to_exit_after_launch = false;
    
    public static void main(String[] args) throws Exception
    {   
        ChooserScreen.start();
    }
    
    public static void launchProgramAndExit() throws Exception
    {
        to_exit_after_launch = true;
        launchProgram();
        System.exit(0);
    }
    
    public static void launchProgramAndContinue(JFrame jframe) throws Exception
    {
        jframe.setVisible(false);
        jframe.dispose();
        System.out.println("---");
        launchProgram();
    }
    
    private static void launchProgram() throws Exception
    {
        program_is_already_started = true;
        BaseUtils.setProperty("program_started", true);
        
        boolean offilneMode = BaseUtils.getPropertyBoolean("offline_mode");
        String serverName = BaseUtils.getPropertyString("server_name");
        if (serverName == null)
        {
            serverName = "";
            if (!offilneMode)
            {
                Frame.reportErr("Конфигурационный файл поврежден. Пожалуйста, перезапустите программу!");
                System.exit(-1); return;
            }
        }
        
        File running_dir = new File(BaseUtils.getParentDirectory(), serverName);
        running_dir.mkdirs();
        
        int memory = BaseUtils.getPropertyInt("memory");
        if (memory == 0) memory = "32".equals(System.getProperty("sun.arch.data.model")) ? 512 : 1024;
        
        ArrayList<String> params = new ArrayList<>();
        
        params.add("java");
        params.add("-Xmx" + memory + "m");
        params.add("-Xms" + memory + "m");
        params.add("-Dsun.java2d.noddraw=true");
        params.add("-Dsun.java2d.d3d=false");
        params.add("-Dsun.java2d.opengl=false");
        params.add("-Dsun.java2d.pmoffscreen=false");
        params.add("-classpath");
        params.add(GuardUtils.appPath());
        params.add(Main.class.getName());
        
        ProcessBuilder pb = new ProcessBuilder(params);
        pb.directory(running_dir);
        Process process = pb.start();
        if (process == null)
            throw new Exception("Не удалось запустить программу!");

        if (!to_exit_after_launch) { new ProcessUtils(process).print(); process.waitFor(); }
    }
    
    public static boolean isStarted()
    {
        return program_is_already_started;
    }
}