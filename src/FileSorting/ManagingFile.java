package FileSorting;

import java.time.*;
import java.time.format.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
/**
 *
 * @author Andrea Marco Giaccherini
 * <amgiaccherini.github.io/amgiaccherini-website/>
 */
public class ManagingFile {
    
    private static final String DATE_PATTERN = "dd/MM/yyyy";

    private boolean viewOldArchive = false;
    
    DateTimeFormatter Formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
    
    File TextFile;
    File TempTextFile;
    File OldTextFile;
    
    public ManagingFile(File file) {
       TextFile = new File(file.getAbsolutePath()+File.separator+"Annotations.txt");
       TempTextFile = new File(file.getAbsolutePath()+File.separator+"Temp-Annotations.txt");
       OldTextFile = new File(file.getAbsolutePath()+File.separator+"OldAnnotations.txt");
       
       RemoveOldDate();
    } 
    
    public void RemoveDate(LocalDate Date) {      
        if(!CheckDayExist(Date)) return;
        
        Map<LocalDate, List<String>> agenda = ReadFile(TextFile);
        
        if (agenda.containsKey(Date)) agenda.remove(Date);
            
        WriteFile(agenda, TempTextFile);
        ReplaceFile(TempTextFile, TextFile);
    } 
    
    private void AddAbilityToFile(File FileToAbilitate, boolean CanRead, boolean CanWrite) {  
        if(!FileToAbilitate.exists()) return;

        if(CanRead)
            FileToAbilitate.setReadable(true);
        
        if (CanWrite)
            FileToAbilitate.setWritable(true);
    }
    
    private void RemoveOldDate() {
        try {
            if (!OldTextFile.exists()) {
                if(OldTextFile.createNewFile()) 
                    AddAbilityToFile(OldTextFile, true, true);
            }
        
            Map<LocalDate, List<String>> agendaMain = ReadFile(TextFile);
            Map<LocalDate, List<String>> agendaTemp = new TreeMap<>(Comparator.reverseOrder());
            
            for(Map.Entry<LocalDate, List<String>> entry : agendaMain.entrySet()) {
                if(entry.getKey().isBefore(LocalDate.now())) {
                    agendaTemp.putIfAbsent(entry.getKey(), agendaMain.get(entry.getKey()));
                    agendaMain.remove(entry.getKey());
                }
            }
            
            WriteFile(agendaMain, TempTextFile);
            ReplaceFile(TempTextFile, TextFile);

            Map<LocalDate, List<String>> agendaOld = ReadFile(OldTextFile);
            agendaOld.putAll(agendaTemp);

            WriteFile(agendaOld, TempTextFile);
            ReplaceFile(TempTextFile, OldTextFile);
        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void AddDay(LocalDate Date) {
        Map<LocalDate, List<String>> agenda = ReadFile(TextFile);
        
        agenda.putIfAbsent(Date, new ArrayList<>());
        
        WriteFile(agenda, TempTextFile);
        
        ReplaceFile(TempTextFile, TextFile);
    }

    private boolean CheckDayExist(LocalDate Date) {
        boolean Exist = false;
        
        Map<LocalDate, List<String>> agenda = ReadFile(TextFile);
        for(Map.Entry<LocalDate, List<String>> entry : agenda.entrySet()) {
            if (entry.getKey().equals(Date)) {
                Exist = true;
                break;
            }
        }
        return Exist;
    }
    
    private void ReplaceFile(File source, File target) {
        try {
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Map<LocalDate, List<String>> ReadFile(File file) {
        Map<LocalDate, List<String>> agenda = new TreeMap<>(Comparator.reverseOrder());
        
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            
            String line;
            LocalDate CurrentDate = null;
            while((line = br.readLine())!= null) {
                if(line.matches("\\d{2}/\\d{2}/\\d{4}")) {
                    CurrentDate = LocalDate.parse(line, Formatter);
                    agenda.putIfAbsent(CurrentDate, new ArrayList<>());
                    continue;
                }
                if (line.equals("--------------") || line.isEmpty())
                    continue;
                if (CurrentDate != null) 
                    agenda.get(CurrentDate).add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        
        return agenda;
    }
    
    private void WriteFile (Map<LocalDate, List<String>> agenda, File file) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            for(Map.Entry<LocalDate, List<String>> entry : agenda.entrySet()) {
                bw.write(entry.getKey().format(Formatter)+"\n\n\n");
                
                for (String obiettivo : entry.getValue())
                    bw.write(obiettivo+"\n\n");
                
                bw.write("--------------\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void EnsureTextFileExists() {
        try {
            if (TextFile.createNewFile()) 
                AddAbilityToFile(TextFile, true, true); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean TryAddDate(LocalDate Date) {
        if (viewOldArchive) return false;
        EnsureTextFileExists();

        if (CheckDayExist(Date)) return false;

        AddDay(Date);
        return true;
    }
    
    public List<LocalDate> GetAllDates() {
        Map<LocalDate, List<String>> agenda = ReadFile(GetCurretViewFile());
        return new ArrayList<>(agenda.keySet());
    }

    public List<String> GetObjectivesByDate(LocalDate date) {
        Map<LocalDate, List<String>> agenda = ReadFile(GetCurretViewFile());
        if (agenda == null || !agenda.containsKey((date))) return new ArrayList<>();
        return new ArrayList<>(agenda.get(date));
    }

    public boolean ToggleObjectiveStatus(LocalDate date, int objectiveIndex) {
        if(viewOldArchive) return false;
        EnsureTextFileExists();

        Map<LocalDate, List<String>> agenda = ReadFile(TextFile);
        if (agenda == null || !agenda.containsKey(date)) return false;

        List<String> objectives = agenda.get(date);
        if (objectiveIndex < 0 || objectiveIndex >= objectives.size()) return false;

        objectives.set(objectiveIndex, BuildObjectiveWithOppositeStatus(objectives.get(objectiveIndex)));

        WriteFile(agenda, TempTextFile);
        ReplaceFile(TempTextFile, TextFile);
        return true;
    }

    public boolean AddObjectiveToDate(LocalDate date, String objectiveText) {
        if (viewOldArchive) return false;

        EnsureTextFileExists();
        if (date == null || objectiveText == null || objectiveText.trim().isEmpty()) return false; 

        Map<LocalDate, List<String>> agenda = ReadFile(TextFile);
        if (agenda == null) return false;
        

        agenda.get(date).add(objectiveText.trim() + "\t" + "\u2716\uFE0F");

        WriteFile(agenda, TempTextFile);
        ReplaceFile(TempTextFile, TextFile);
        return true;
    }

    private String BuildObjectiveWithOppositeStatus(String objectiveLine) {
    return objectiveLine.contains("\u2714\uFE0F") ?
        objectiveLine.replace("\u2714\uFE0F", "\u2716\uFE0F") :
        objectiveLine.replace("\u2716\uFE0F", "\u2714\uFE0F");
    }

    private void EnsureOldFileExists() {
        try {
            if(OldTextFile.createNewFile()) 
                AddAbilityToFile(OldTextFile, viewOldArchive, viewOldArchive);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File GetCurretViewFile() {
        if(viewOldArchive) {
            EnsureOldFileExists();
            return OldTextFile;
        }
        EnsureTextFileExists();
        return TextFile;
    }

    public void SetViewOldArchive(boolean viewOldArchive) {this.viewOldArchive = viewOldArchive;}
    public boolean GetViewOldArchive() {return viewOldArchive;}
}