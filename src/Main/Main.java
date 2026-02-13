package Main;

import FileSorting.*;
import Menu.GestioneMenu;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
/**
 *
 * @author Andrea Marco Giaccherini
 * <amgiaccherini.github.io/amgiaccherini-website/>
 */
public class Main {
    
    private static int Scelta = 0;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    static File file;

    public static void main(String[] args) {
        
        LocalDate Today = LocalDate.now(); 
        
        GestioneMenu Menu = new GestioneMenu();
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

        File.CheckObjectives();
        System.out.println();
        
        do {
            Scelta = Menu.StampaMenu();
            
            System.out.println();
            
            switch(Scelta) {
                case 1 -> {
                    System.out.println("Scegli la data:\n");
                    LocalDate Date = Menu.GestioneDate();
                    File.AddObjectives(Date);
                    break;
                }
                case 2 -> {
                    System.out.println("Scegli la data:\n");
                    LocalDate Date = Menu.GestioneDate();
                    File.TickObjectives(File.FindCrossedObjectives(Date), Date);
                    break;
                }
                case 3 -> {
                    File.StampEveryDate();
                    break;
                }
                case 4 -> {
                    System.out.println("Scegli la data:\n");
                    LocalDate Date = Menu.GestioneDate();
                    File.StampObjective(Date);
                    break;
                }
                case 5 -> {
                    System.out.println("Scegli la data:\n");
                    LocalDate Date = Menu.GestioneDate();
                    File.RemoveDate(Date);
                    break;
                }
                case 6 -> {
                    System.out.println("Arrivederci!");
                    break;
                }
            } 
        } while(Scelta != 6);
        System.out.println("Il Programma è terminato con successo tutti i cambiamenti sono permanenti.");
    }
}