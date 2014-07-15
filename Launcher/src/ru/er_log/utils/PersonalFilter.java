package ru.er_log.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;

public class PersonalFilter extends FileFilter {

    private final int type;

    public PersonalFilter(int i)
    {
        type = i;
    }

    @Override
    public boolean accept(File f)
    {
        if (f.isDirectory())
        {
            return true;
        }

        try
        {
            String extension = getExtension(f);
            if (extension != null)
            {
                if (extension.equals("png"))
                {
                    BufferedImage img = ImageIO.read(f);
                    if (type == 0)
                    {
                        if (img.getWidth() == 64 && img.getHeight() == 32)
                            return true;
                    } else
                    {
                        if (img.getWidth() == 64 && img.getHeight() == 32) return true;
                        else if (img.getWidth() == 22 && img.getHeight() == 17) return true;
                    }
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public String getDescription()
    {
        return (type == 0 ? "Файл скина в формате \"PNG\" (64x32)" : "Файл плаща в формате \"PNG\" (64x32 или 22x17)");
    }

    public static String getExtension(File f)
    {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1)
        {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
