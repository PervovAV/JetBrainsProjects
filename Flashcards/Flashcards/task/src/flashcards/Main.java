package flashcards;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, String> arguments = new HashMap<>();

        if (args.length % 2 != 0) {
            System.out.println("Incorrect command-line arguments");
            return;
        }

        if (args.length > 1) {
            for (int i = 0; i < args.length; i += 2) {
                arguments.put(args[i], args[i + 1]);
            }
        }

        String fileNameImport = arguments.getOrDefault("-import", "noImport");
        String fileNameExport = arguments.getOrDefault("-export", "noExport");
        CardWorker cardWorker = new CardWorker();
        cardWorker.start(fileNameImport, fileNameExport);
    }
}
