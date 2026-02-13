package Menu;

import java.util.*;
import java.time.LocalDate;
/**
 *
 * @author Andrea Marco Giaccherini
 * <amgiaccherini.github.io/amgiaccherini-website/>
 */
public class GestioneMenu {
    
    private final Scanner sc = new Scanner(System.in);
    
    public int StampaMenu() {
        System.out.println("Cosa vuoi fare ora ?\n");
        System.out.println("1) Aggiungere obiettivi");
        System.out.println("2) Segnare obiettivi");
        System.out.println("3) Stampa tutte le date");
        System.out.println("4) Leggi gli obiettivi");
        System.out.println("5) Rimuovi una data");
        System.out.println("6) Esci\n");
        
        return GestioneInt();
    }
    
    private int GestioneInt() {
        int Scelta = 0;
        
        while (Scelta == 0) {
            try {
                Scelta = sc.nextInt();
                
                if (!(Scelta > 0) && !(Scelta < 8)) {
                    System.out.println("Valore fuori dal range, Riprova.");
                    Scelta = 0;
                } 
            } catch (Exception e) {
                System.out.println("Questo valore non è un numero, Riprova.");
                sc.next();
            }
        }
        return Scelta;
    }
    
    public boolean GestioneBool() {
        while (true) {
                String input = sc.nextLine().toLowerCase();
                
                if (input.matches("si")) 
                    return true;
                else if (input.matches("no"))
                    return false;
                else 
                    System.out.println("Valore non valido, Riprova.");
        }
    }
    
    public LocalDate GestioneDate() {
        int Month = 0;
        int Day = 0;
        int Year = 0;
        
        while(Year == 0) {
            try {
                System.out.println("Scegli l'anno.");
                Year = sc.nextInt();
                
                if (Year < LocalDate.now().getYear()) {
                    System.out.println("Valore non valido, Riprova.");
                    Year = 0;
                }
            } catch (InputMismatchException e) {
                System.out.println("Questo valore non è un numero, Riprova.");
                sc.next();
            }
        }
        while (Month == 0) {
            try {
                System.out.println("Scegli il mese.");
                Month = sc.nextInt();

                if (Month > 12 || Month < 1) {
                    if (LocalDate.now().getMonthValue() > Month && Year == LocalDate.now().getYear())
                    System.out.println("Valore non valido, Riprova.");
                    Month = 0;
                }
            } catch (InputMismatchException e) {
                System.out.println("Questo valore non è un numero, Riprova.");
                sc.next();
            }
        }  
        while (Day == 0) {
            try {
                System.out.println("Scegli il giorno.");
                Day = sc.nextInt();
                
                if (Day > 31 || Day < 1) {
                    System.out.println("Valore non valido, Riprova.");
                    Day = 0;
                } else if (Day >= LocalDate.now().lengthOfMonth()) {
                    System.out.println("Valore non valido, Riprova.");
                    Day = 0;
                } else if(LocalDate.now().getDayOfMonth() > Day && Month == LocalDate.now().getMonthValue() && Year == LocalDate.now().getYear()) {
                    System.out.println("Valore non valido, Riprova.");
                    Day = 0;  
                }
            } catch (InputMismatchException e) {
                System.out.println("Questo Valore non è un numero, Riprova.");
                sc.next();
            }
        }
        
        return LocalDate.of(Year, Month, Day);
    }
}