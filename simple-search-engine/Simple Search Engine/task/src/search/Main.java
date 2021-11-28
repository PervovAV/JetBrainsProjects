package search;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Wrong arguments");
            return;
        }

        Map<String, String> arguments = new HashMap<>();
        arguments.put(args[0], args[1]);

        String fileName = arguments.get("--data");

        SearchEngine searchEngine = new SearchEngine();
        searchEngine.start(fileName);
    }
}
