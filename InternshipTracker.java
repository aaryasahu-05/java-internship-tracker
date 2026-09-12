import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class InternshipTracker {

    // Represents one internship application we're tracking
    static class Internship {
        String name;
        LocalDate deadline;
        String status; // "Not Applied" or "Applied"

        Internship(String name, LocalDate deadline, String status) {
            this.name = name;
            this.deadline = deadline;
            this.status = status;
        }

        long daysLeft() {
            return ChronoUnit.DAYS.between(LocalDate.now(), deadline);
        }

        String toFileLine() {
            return name + "," + deadline + "," + status;
        }
    }

    static final String FILE_NAME = "internships.txt";
    static List<Internship> internships = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        loadFromFile();
        boolean running = true;

        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": addInternship(); break;
                case "2": viewInternships(); break;
                case "3": markAsApplied(); break;
                case "4":
                    saveToFile();
                    System.out.println("Saved. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
        sc.close();
    }

    static void printMenu() {
        System.out.println("\n=== Internship Deadline Tracker ===");
        System.out.println("1. Add internship");
        System.out.println("2. View all (sorted by deadline)");
        System.out.println("3. Mark as applied");
        System.out.println("4. Save and exit");
        System.out.print("Choose an option: ");
    }

    static void addInternship() {
        System.out.print("Internship name: ");
        String name = sc.nextLine().trim();

        LocalDate date = null;
        while (date == null) {
            System.out.print("Deadline (yyyy-MM-dd): ");
            String input = sc.nextLine().trim();
            try {
                date = LocalDate.parse(input, fmt);
            } catch (Exception e) {
                System.out.println("Invalid date format, try again.");
            }
        }

        internships.add(new Internship(name, date, "Not Applied"));
        System.out.println("Added: " + name);
    }

    static void viewInternships() {
        if (internships.isEmpty()) {
            System.out.println("No internships tracked yet.");
            return;
        }

        internships.sort(Comparator.comparing(i -> i.deadline));

        System.out.println("\n--- Internships (soonest deadline first) ---");
        for (int i = 0; i < internships.size(); i++) {
            Internship it = internships.get(i);
            long days = it.daysLeft();
            String urgency = days < 0 ? "PASSED" : (days <= 3 ? "URGENT (" + days + " days left)" : days + " days left");
            System.out.printf("%d. %-25s Deadline: %-12s Status: %-12s %s%n",
                    i + 1, it.name, it.deadline, it.status, urgency);
        }
    }

    static void markAsApplied() {
        viewInternships();
        if (internships.isEmpty()) return;

        System.out.print("Enter the number of the internship to mark as applied: ");
        try {
            int index = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (index >= 0 && index < internships.size()) {
                internships.get(index).status = "Applied";
                System.out.println("Updated.");
            } else {
                System.out.println("Invalid number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Internship it : internships) {
                writer.write(it.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    internships.add(new Internship(parts[0], LocalDate.parse(parts[1], fmt), parts[2]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }
}
