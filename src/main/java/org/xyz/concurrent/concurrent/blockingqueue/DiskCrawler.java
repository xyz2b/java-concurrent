package org.xyz.concurrent.concurrent.blockingqueue;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingDeque;

public class DiskCrawler {
    public static final int BOUND = 100;
    public static final int N_CONSUMERS = 10;

    public static final ConcurrentSkipListSet<File> indexedList = new ConcurrentSkipListSet<File>();

    public static boolean alreadyIndexed(File file) {
        return indexedList.contains(file);
    }

    public static void indexFile(File file) {
        System.out.println("index file: " + file.getName());
        indexedList.add(file);
    }

    public static void main(String[] args) {
        BlockingQueue<File> queue = new LinkedBlockingDeque<File>(BOUND);

        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        };

        File[] roots = new File[10];
        roots[0] = new File("/");

        for (File root : roots) {
            new Thread(new FileCrawler(queue, filter, root)).start();
        }

        for (int i = 0; i < N_CONSUMERS; i++) {
            new Thread(new Indexer(queue)).start();
        }

    }

}
