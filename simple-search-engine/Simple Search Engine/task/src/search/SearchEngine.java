package search;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SearchEngine {

    public void start(String fileName) {
        fillDataFromFile(fileName);
        makeWordsAndIdx();
        Scanner scanner = new Scanner(System.in);
        boolean isContinued = true;

        while(isContinued) {
            System.out.println("=== Menu ===");
            System.out.println("1. Find a person");
            System.out.println("2. Print all people");
            System.out.println("0. Exit");
            int choice = scanner.nextInt();

            switch (choice) {
                case 0 :
                    System.out.println();
                    System.out.println("Bye!");
                    isContinued = false;
                    break;
                case 1 :
                    System.out.println();
                    findPerson();
                    System.out.println();
                    break;
                case 2 :
                    System.out.println();
                    printAllPeople();
                    System.out.println();
                    break;
                default:
                    System.out.println();
                    System.out.println("Incorrect option! Try again.");
                    System.out.println();
                    break;
            }
        }
    }

    private void fillDataFromFile(String fileName) {
        File file = new File(fileName);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                lines.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void findPerson() {
        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        Scanner scanner = new Scanner(System.in);
        String strategy = scanner.nextLine();
        System.out.println();
        System.out.println("Enter a name or email to search all suitable people.");
        String searchWords = scanner.nextLine();

        StrategyHelper strategyHelper = new StrategyHelper(strategy, searchWords);
        strategyHelper.findByStrategy();
    }

    private void makeWordsAndIdx() {
        for (int i = 0; i < lines.size(); i++) {
            String[] words = lines.get(i).trim().toLowerCase().split(" ");
            for (String word : words) {
                HashSet<Integer> set = wordsAndIdx.computeIfAbsent(word, k -> new HashSet<>());
                set.add(i);
            }
        }

        for (var el : wordsAndIdx.entrySet()) {
            System.out.println(el.getKey() + " -> " + el.getValue());
        }
    }

    private void printAllPeople() {
        System.out.println("=== List of people ===");
        for (String str : lines) {
            System.out.println(str);
        }
    }


    private class StrategyHelper {

        public StrategyHelper(String strategy, String searchWords) {
            this.strategy = strategy;
            this.searchWords = searchWords.trim().toLowerCase().split(" ");
        }

        public void findByStrategy() {
            switch (strategy) {
                case "ALL" :
                    findAll();
                    break;
                case "ANY" :
                    findAny();
                    break;
                case "NONE" :
                    findNone();
                    break;
                default:
                    System.out.println("Wrong input");
            }
        }

        private void findAll() {
            ArrayList<Set<Integer>> sets = new ArrayList<>();

            for (String word : searchWords) {
                if (!wordsAndIdx.containsKey(word)) {
                    idxCorrectLines.clear();
                    sets.clear();
                    break;
                } else {
                    sets.add(wordsAndIdx.get(word));
                }
            }

            if (!(sets.size() == 0)) {
                idxCorrectLines = sets.get(0);

                for (int i = 1; i < sets.size(); i++) {
                    idxCorrectLines.retainAll(sets.get(i));
                }
            }

            printAnswer();
        }

        private void findAny() {
            for (String word : searchWords) {
                if (wordsAndIdx.containsKey(word)) {
                    idxCorrectLines.addAll(wordsAndIdx.get(word));
                }
            }
            printAnswer();
        }

        private void findNone() {
            for (int i = 0; i < lines.size(); i++) {
                idxCorrectLines.add(i);
            }

            for (String word : searchWords) {
                if (wordsAndIdx.containsKey(word)) {
                    idxCorrectLines.removeAll(wordsAndIdx.get(word));
                }
            }

            printAnswer();
        }

        private void printAnswer() {
            if (idxCorrectLines.size() != 0) {
                System.out.println(idxCorrectLines.size() + " persons found:");

                for (Integer idx : idxCorrectLines) {
                    System.out.println(lines.get(idx));
                }
            } else {
                System.out.println("No matching people found.");
            }
        }

        private String strategy;
        private String[] searchWords;
        private Set<Integer> idxCorrectLines = new HashSet<>();
    }

    private ArrayList<String> lines = new ArrayList<>();
    private Map<String, HashSet<Integer>> wordsAndIdx = new HashMap<>();
}
