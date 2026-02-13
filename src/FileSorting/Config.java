package FileSorting;

import java.io.*;
import java.util.*;
import javax.swing.*;
/**
 *
 * @author Andrea Marco Giaccherini
 * <amgiaccherini.github.io/amgiaccherini-website/>
 */
public class Config {
    
    private static final String USERHOME = System.getProperty("user.home");
    
    private static final String CONFIGPATH = USERHOME+File.separator+"DailyObjectives"+File.separator+"config.properties";
    
    private final Properties props;
    
    File ConfigFile;
    
    public Config() {
        ConfigFile = new File(CONFIGPATH);
        props = new Properties();
        load();
    }
    
    private void load() {
        try {
            if (ConfigFile.exists()) {
                try (FileInputStream fis = new FileInputStream(ConfigFile)) {
                    props.load(fis);
                } catch (IOException e) {
                    System.out.println("Errore: "+e);
                }
            } else {
                ConfigFile.getParentFile().mkdirs();
                ConfigFile.createNewFile();
                save(); 
            }
        } catch (IOException e) {
            System.out.println("Errore: "+e);
        }
    }
    
    public void save() {
        try (FileOutputStream fos = new FileOutputStream(ConfigFile)) {
            props.store(fos, "Configurazione applicazione"); 
        } catch (IOException e) {
            System.out.println("Errore: "+e);
        }
    }
    
    public String GetConfig(String key) {
        return props.getProperty(key);
    }
    
    public void SetConfig(String key, String value) {
        props.setProperty(key, value);
    }
    
    public static File ScegliCartella() {
        JFileChooser chooser = new JFileChooser();
        
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Seleziona la cartella dove salvare i file");
        chooser.setAcceptAllFileFilterUsed(false);
        
        if(chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            System.out.println("Scelta annullata, chiusura del programma!");
            System.exit(0);
        }
        return chooser.getSelectedFile();
    }
    
}
