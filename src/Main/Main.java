package Main;

import FileSorting.*;
import GUI.Window;
import java.io.*;
import java.nio.file.*;
import javax.swing.SwingUtilities;
/**
 *
 * @author Andrea Marco Giaccherini
 * <amgiaccherini.github.io/amgiaccherini-website/>
 */
public class Main {
    
    private static File file;

    public static void main(String[] args) {
        Config config = new Config();
                
        if(config.GetConfig("FolderPath") == null || config.GetConfig("FolderPath").isEmpty()) {
            file = Config.ScegliCartella();
            config.SetConfig("FolderPath", file.getAbsolutePath());
            config.save();
        } else {
            file = new File(config.GetConfig("FolderPath"));
            if (!Files.exists(file.toPath())) {
                file = Config.ScegliCartella();
                config.SetConfig("FolderPath", file.getAbsolutePath());
                config.save();
            }
        }
        
        ManagingFile File = new ManagingFile(file);
        SwingUtilities.invokeLater(() -> {
            Window window = new Window(file);
            window.setVisible(true);
        });
    }
}
