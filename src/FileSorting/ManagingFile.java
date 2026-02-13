package FileSorting;

import Menu.GestioneMenu;
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
    private static final String TICK = "\u2714️";
    private static final String CROSS = "\u2716\uFE0F";
    
    private final Scanner sc = new Scanner(System.in);
    
    ArrayList<String> CrossedObjectives = new ArrayList<>();
    
    DateTimeFormatter Formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
    
    File TextFile;
    File TempTextFile;
    File OldTextFile;
    
    GestioneMenu Risposta = new GestioneMenu();
    
    public ManagingFile(File file) {
       TextFile = new File(file.getAbsolutePath()+File.separator+"Annotations.txt");
       TempTextFile = new File(file.getAbsolutePath()+File.separator+"Temp-Annotations.txt");
       OldTextFile = new File(file.getAbsolutePath()+File.separator+"OldAnnotations.txt");
    } 
    
    public void CheckObjectives() {
        try
        {
            if (TextFile.createNewFile())
            {
                System.out.println("File Creato nella Path Designata!");
                AddAbilityToFile(TextFile, true, true);
            } else {
                System.out.println("File Già Esistente!");
                AddAbilityToFile(TextFile, true, true);
            }
        } catch (IOException e) {
            System.out.println("Impossibile Creare il File.");
        }
        
        LocalDate Today = LocalDate.now();
        CrossedObjectives = FindCrossedObjectives(Today);
        
        if (!CrossedObjectives.isEmpty()) {
            TickObjectives(CrossedObjectives, Today);
        }
        
        Map<LocalDate, List<String>> agenda = ReadFile(TextFile);
        
        if(!agenda.containsKey(Today.plusDays(1))) {
            System.out.println("Vuoi Aggiungere gli obiettivi di domani ?");
            if(Risposta.GestioneBool()) {
                AddObjectives(Today.plusDays(1));
            }
        }  
        RemoveOldDate();
    }
    
    public ArrayList<String> FindCrossedObjectives(LocalDate Date) {
        if (!CheckDayExist(Date)) {
            System.out.println("Non è stato trovata "+Date.format(Formatter)+" all'interno del file, vuoi aggiungerla ?");
            if(Risposta.GestioneBool()) {
                AddDay(Date);
            } else {
                System.out.println("Non è stato aggiunto il giorno.");
                return new ArrayList<>();
            }
        } 
        
        ArrayList<String> CrossedObjectivesToReturn = new ArrayList<>();
        
        Map<LocalDate, List<String>> agenda = ReadFile(TextFile);
        
        if(agenda.containsKey(Date)) {
            List<String> list = agenda.get(Date);
            for (String obiettivo : list) {
                if (obiettivo.endsWith(CROSS)) {
                    CrossedObjectivesToReturn.add(obiettivo);
                }
            }
        }
        
        return CrossedObjectivesToReturn;
    }
    
    public void TickObjectives(ArrayList<String> CrossedObjectives, LocalDate Date) {     
        for (String Objective : CrossedObjectives) {
            System.out.println("\nHai Completato "+Objective.replace("\t"+CROSS, "")+" ?");
            if (Risposta.GestioneBool()) {
                Map<LocalDate, List<String>> agenda = ReadFile(TextFile);
               
                List<String> list = agenda.get(Date);
                for (int i = 0; i < list.size(); i++) {
                    if(list.get(i).matches(Objective)) {
                        String TickedObjective = list.get(i).replace(CROSS, TICK);
                        list.set(i, TickedObjective);
                        break;
                    }
                }
                WriteFile(agenda, TempTextFile);
                ReplaceFile(TempTextFile, TextFile); 
            }
        }
        if (CrossedObjectives.isEmpty()) {
            System.out.println("\nNon sono stai trovati obiettivi.");
        }
        System.out.println();
    }
    
    public void AddObjectives(LocalDate Date) {
        if (!CheckDayExist(Date)) {
            AddDay(Date);
        }
        
        Map<LocalDate, List<String>> agenda = ReadFile(TextFile);
        
        do {
            System.out.println("\nChe obiettivo vuoi aggiungere ?");
            agenda.get(Date).add(sc.nextLine()+"\t"+CROSS);
                
            System.out.println("Vuoi continuare ?");
        } while (Risposta.GestioneBool());
        
        System.out.println();
        
        WriteFile(agenda, TempTextFile);
        ReplaceFile(TempTextFile, TextFile);
    }
    
    public void RemoveDate(LocalDate Date) {      
        if(!CheckDayExist(Date)) {
            System.out.println("\nData non trovata!\n");
            return;
        }
        
        Map<LocalDate, List<String>> agenda = ReadFile(TextFile);
        
        if (agenda.containsKey(Date)) {
                agenda.remove(Date);
                System.out.println("\nData rimossa!\n");
        }
        
        WriteFile(agenda, TempTextFile);
        ReplaceFile(TempTextFile, TextFile);
    }
    
    public void StampEveryDate() {
        Map<LocalDate, List<String>> agenda = ReadFile(TextFile);
        
        System.out.println("--------------");
        
        for(Map.Entry<LocalDate, List<String>> entry : agenda.entrySet()) {
            System.out.println(entry.getKey().format(Formatter));
        }
        
        System.out.println("--------------\n");
    }
    
    public void StampObjective(LocalDate Date) {
        Map<LocalDate, List<String>> agenda = ReadFile(TextFile);
        
        System.out.println("\n--------------");
        
        for(Map.Entry<LocalDate, List<String>> entry : agenda.entrySet()) {
            if(entry.getKey().equals(Date)) {
                System.out.println(Date.format(Formatter)+"\n");
                for (String obiettivo : entry.getValue()) {
                    System.out.println(obiettivo);
                }
                System.out.println("--------------\n");
                break;
            }
        }
    }
    
    private void AddAbilityToFile(File FileToAbilitate, boolean CanRead, boolean CanWrite) {  
        if(!FileToAbilitate.exists()) {
            System.out.println("Il file non esiste.");
            return;
        }

        if(CanRead) {
            FileToAbilitate.setReadable(true);
        }
        
        if (CanWrite) {
            FileToAbilitate.setWritable(true);
        }
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
            System.out.println("Errore: "+e);
        }
    }
    
    private void AddDay(LocalDate Date) {
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
            System.out.println("Errore: "+e);
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
            System.out.println("Errore: "+e);
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
            System.out.println("Errore: "+e);
        }
    }
}