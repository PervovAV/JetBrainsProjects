package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CardWorker {

    public void start(String fileNameImport, String fileNameExport) {
        log.add("\n");

        if (!"noImport".equals(fileNameImport)) {
            importCards(fileNameImport);
        }

        boolean isContinued = true;
        Scanner scanner = new Scanner(System.in);

        while (isContinued) {
            System.out.println("Input the action (add, remove, import, export, ask, exit, " +
                    "log, hardest card, reset stats):");
            log.add("Input the action (add, remove, import, export, ask, exit, " +
                    "log, hardest card, reset stats):\n");
            String choice = scanner.nextLine();
            log.add(choice + "\n");

            switch (choice) {
                case "add" :
                    addCard();
                    break;
                case "remove":
                    removeCard();
                    break;
                case  "import" :
                    importCards("noFile");
                    break;
                case "export" :
                    exportCards("noFile");
                    break;
                case "ask" :
                    ask();
                    break;
                case "exit" :
                    if (!"noExport".equals(fileNameExport)) {
                        exportCards(fileNameExport);
                    }
                    isContinued = false;
                    System.out.println("Bye bye!");
                    break;
                case "log" :
                    writeLogToFile();
                    break;
                case "hardest card" :
                    showHardestCard();
                    break;
                case "reset stats" :
                    resetStats();
                    break;
                default:
                    isContinued = false;
                    break;
            }
        }
    }

    private void addCard() {
        System.out.println("The card:");
        log.add("The card:\n");
        Scanner scanner = new Scanner(System.in);
        String term = scanner.nextLine();
        log.add(term + "\n");

        if (cardsMap.containsKey(term)) {
            System.out.printf("The card \"%s\" already exists.\n", term);
            log.add(String.format("The card \"%s\" already exists.\n", term));
            return;
        }

        System.out.println("The definition of the card:");
        log.add("The definition of the card:\n");
        String definition = scanner.nextLine();
        log.add(definition + "\n");

        if (cardsMap.containsValue(definition)) {
            System.out.printf("The definition \"%s\" already exists.\n", definition);
            log.add(String.format("The definition \"%s\" already exists.\n", definition));
        }

        cardsMap.put(term, definition);
        termsMistakes.put(term, 0);
        System.out.printf("The pair (\"%s\":\"%s\") has been added.\n", term, definition);
        log.add(String.format("The pair (\"%s\":\"%s\") has been added.\n", term, definition));
    }

    private void removeCard() {
        System.out.println("Which card?");
        log.add("Which card?\n");
        Scanner scanner = new Scanner(System.in);
        String term = scanner.nextLine();
        log.add(term + "\n");

        if (cardsMap.containsKey(term)) {
            cardsMap.remove(term);
            termsMistakes.remove(term);
            System.out.println("The card has been removed.");
            log.add("The card has been removed.\n");
        } else {
            System.out.printf("Can't remove \"%s\": there is no such card.", term);
            log.add(String.format("Can't remove \"%s\": there is no such card.", term));
        }
    }

    private void importCards(String fileNameImport) {
        String fileName = "";
        if ("noFile".equals(fileNameImport)) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("File name:");
            log.add("File name:\n");
            fileName = scanner.nextLine();
        } else {
            fileName = fileNameImport;
        }

        log.add(fileName + "\n");
        File file = new File(fileName);
        int counter = 0;

        if (!file.exists()) {
            System.out.println("File not found.");
            log.add("File not found.\n");
        } else {
            try {
                Scanner sc = new Scanner(file);

                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    line = line.trim();
                    if (!line.isEmpty()) {
                        if (line.charAt(0) == '#') {
                            continue;
                        }
                        String[] info = line.split(":");
                        cardsMap.put(info[0], info[1]);
                        termsMistakes.put(info[0], Integer.valueOf(info[2]));
                        counter++;
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        System.out.println(counter + " cards have been loaded.");
        log.add(counter + " cards have been loaded.\n");
    }

    private void exportCards(String fileNameExport) {
        String fileName = "";
        if ("noFile".equals(fileNameExport)) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("File name:");
            log.add("File name:\n");
            fileName = scanner.nextLine();
        } else {
            fileName = fileNameExport;
        }

        log.add(fileName + "\n");

        try (FileWriter writer = new FileWriter(fileName)) {
            for (var card : cardsMap.entrySet()) {
                int nMistakes = termsMistakes.get(card.getKey());
                writer.write(card.getKey());
                writer.write(":");
                writer.write(card.getValue());
                writer.write(":");
                writer.write(String.valueOf(nMistakes));
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(cardsMap.size() + " cards have been saved.");
        log.add(cardsMap.size() + " cards have been saved.\n");
    }

    private void ask() {
        System.out.println("How many times to ask?");
        log.add("How many times to ask?\n");
        Scanner scanner = new Scanner(System.in);
        int nTimes = scanner.nextInt();
        log.add(nTimes + "\n");
        scanner.nextLine();

        for (var card : cardsMap.entrySet()) {
            if (nTimes > 0) {
                System.out.println("Print the definition of \"" + card.getKey() + "\":");
                log.add("Print the definition of \"" + card.getKey() + "\":\n");
                String answer = scanner.nextLine();
                log.add(answer + "\n");

                if (answer.equals(card.getValue())) {
                    System.out.println("Correct!");
                    log.add("Correct!\n");
                } else {
                    if (cardsMap.containsValue(answer)) {
                        String termWrong = "";

                        for (var enSet : cardsMap.entrySet()) {
                            if (answer.equals(enSet.getValue())) {
                                termWrong = enSet.getKey();
                            }
                        }

                        System.out.println("Wrong. The right answer is \"" +
                                card.getValue() + "\", but your definition is correct for " +
                                "\"" + termWrong + "\".");
                        log.add("Wrong. The right answer is \"" +
                                card.getValue() + "\", but your definition is correct for " +
                                "\"" + termWrong + "\".\n");
                    } else {
                        System.out.println("Wrong. The right answer is \"" +
                                card.getValue() + "\"");
                        log.add("Wrong. The right answer is \"" +
                                card.getValue() + "\"\n");
                    }
                    termsMistakes.put(card.getKey(), termsMistakes.get(card.getKey()) + 1);
                }
            } else {
                break;
            }
            nTimes--;
        }
    }

    private void resetStats() {
        for (var el : termsMistakes.entrySet()) {
            el.setValue(0);
        }

        System.out.println("Card statistics have been reset.");
        log.add("Card statistics have been reset.\n");
    }

    private void showHardestCard() {
        int maxMistakes = 0;

        for (var el : termsMistakes.entrySet()) {
            if (maxMistakes < el.getValue()) {
                maxMistakes = el.getValue();
            }
        }

        ArrayList<String> hardestCards = new ArrayList<>();

        for (var el : termsMistakes.entrySet()) {
            if (maxMistakes == el.getValue()) {
                hardestCards.add(el.getKey());
            }
        }

        if (maxMistakes == 0) {
            System.out.println("There are no cards with errors.");
            log.add("There are no cards with errors.\n");
        } else if (hardestCards.size() == 1) {
            System.out.println("The hardest card is \"" + hardestCards.get(0) + "\"." +
                    " You have " + maxMistakes + " errors answering it.");
            log.add("The hardest card is \"" + hardestCards.get(0) + "\"." +
                    " You have " + maxMistakes + " errors answering it.\n");
        } else {
            System.out.print("The hardest cards are");
            log.add("The hardest cards are");
            for (int i = 0; i < hardestCards.size(); i++) {
                if (i == hardestCards.size() - 1) {
                    System.out.printf(" \"%s\".", hardestCards.get(i));
                    log.add(String.format(" \"%s\".", hardestCards.get(i)));
                } else {
                    System.out.printf(" \"%s\",", hardestCards.get(i));
                    log.add(String.format(" \"%s\",", hardestCards.get(i)));
                }
            }
            System.out.println(" You have " + maxMistakes + " errors answering them.");
            log.add(" You have " + maxMistakes + " errors answering them.\n");
        }
    }

    private void writeLogToFile() {
        System.out.println("File name:");
        log.add("File name:\n");
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();
        log.add(fileName + "\n");
        System.out.println("The log has been saved.");
        log.add("The log has been saved.\n");

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            for (String el : log) {
                fileWriter.write(el);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> cardsMap = new LinkedHashMap<>();
    private Map<String, Integer> termsMistakes = new LinkedHashMap<>();
    private ArrayList<String> log = new ArrayList<>();
}
