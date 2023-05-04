package pcd.lab06.executors.forkjoin.documentsearch;

import java.io.*;

public class WordCounterMain {    


    public static void main(String[] args) throws IOException {
        final String rootDirectory = args[0];
        final int repeatCount = Integer.parseInt(args[2]);
        final String searchedWord = args[1];
        final WordCounter wordCounter = new WordCounter();
        final Folder folder = Folder.fromDirectory(new File(rootDirectory));
        long counts;
        long startTime;
        long stopTime;
        long[] singleThreadTimes = new long[repeatCount];
        long[] forkedThreadTimes = new long[repeatCount];
        for (int i = 0; i < repeatCount; i++) {
            startTime = System.currentTimeMillis();
            counts = wordCounter.countOccurrencesOnSingleThread(folder, searchedWord);
            stopTime = System.currentTimeMillis();
            singleThreadTimes[i] = (stopTime - startTime);
            System.out.println(counts + " , single thread search took " + singleThreadTimes[i] + "ms");
        }
        for (int i = 0; i < repeatCount; i++) {
            startTime = System.currentTimeMillis();
            counts = wordCounter.countOccurrencesInParallel(folder, searchedWord);
            stopTime = System.currentTimeMillis();
            forkedThreadTimes[i] = (stopTime - startTime);
            System.out.println(counts + " , fork / join search took " + forkedThreadTimes[i] + "ms");
        }
        System.out.println("\nCSV Output:\n");
        System.out.println("Single thread,Fork/Join");        
        for (int i = 0; i < repeatCount; i++) {
            System.out.println(singleThreadTimes[i] + "," + forkedThreadTimes[i]);
        }
        System.out.println();
    }
}
