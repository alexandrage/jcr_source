package ru.er_log.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import ru.er_log.components.Frame;

public class ProcessUtils {
    
    private Process process = null;
    
    public ProcessUtils(Process process)
    {
        this.process = process;
    }
    
    public void print() { print(""); }
    
    public void print(final String prefix)
    {
        Thread errorThread = new Thread() { public void run() { print(prefix, true); } };
        errorThread.setName("errorStream");
        errorThread.start();
        
        print(prefix, false);
    }
    
    private void print(String prefix, boolean isErrorStream)
    {
        try
        {
            InputStream inputStream;
            if (isErrorStream) inputStream = process.getErrorStream();
            else inputStream = process.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream, System.getProperty("file.encoding"));
            BufferedReader buf = new BufferedReader(reader);
            String line = null;
            
            while (isRunning())
            {
                try
                {
                    while ((line = buf.readLine()) != null)
                    {
                        if (isErrorStream)
                            Frame.reportErr(line);
                        else
                            Frame.report(prefix + line);
                    }
                } catch (IOException ex)
                {
                    ex.printStackTrace();
                } finally
                {
                    try
                    {
                        buf.close();
                    } catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (UnsupportedEncodingException e)
        {
            Frame.reportErr("Не удалось установить кодировку при выводе сообщений об отладке");
            e.printStackTrace();
        }
    }
    
    public boolean isRunning()
    {
        try
        {
            process.exitValue();
        } catch (IllegalThreadStateException ex)
        {
            return true;
        }
        
        System.exit(0);
        return false;
    }
}
