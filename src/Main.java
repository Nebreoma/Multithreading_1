import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.String.join;

public class Main {

    public static void main(String[] args) throws ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (String text : texts) {

            Future<Map<String, Integer>> future = executor.submit(() -> {

                int maxSize = 0;
                Map<String, Integer> map = new HashMap<>();
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                map.put(text, maxSize);
                return map;
            });//конец лямбды future

            futures.add(future);
        }

        executor.shutdown();

        for (Future<Map<String, Integer>> f : futures) {
            try {
                Map<String, Integer> map = f.get();
                String text = map.keySet().toString();
                System.out.println(text.substring(0, 100) + " -> " + map.values());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
    }//конец main

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}

//первоначальный код = 59 014 ms
//Код в main = 8 103 ms
//Код в max = 8 920 ms