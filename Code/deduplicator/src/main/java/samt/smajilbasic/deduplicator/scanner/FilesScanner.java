package samt.smajilbasic.deduplicator.scanner;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * FilesScanner
 */
public class FilesScanner extends Thread {

    private List<String> paths;
    private List<String> ignorePaths;
    private LinkedList<String> scanPaths;

    public FilesScanner(List<String> paths, List<String> ignorePaths, List<String> ignoreFiles) {
        super();
        this.ignorePaths = ignorePaths;
        this.paths = paths;
        this.scanPaths = new LinkedList<String>();
    }

    @Override
    public void run() {

        LinkedList<String> tempList = new LinkedList<>();

        for (String string : paths) {
            System.out.println("Path: " + string);
        }

        for (String string : tempList) {
            System.out.println("Path: " + string);
        }
        boolean add = true;
        long start = System.currentTimeMillis();

        while (tempList.peek() != null) {
            String path = tempList.poll();
            File file = new File(path);
            if (file.isFile()) {
                add = true;
                for (String ignorePath : ignorePaths) {
                    if (path.startsWith(ignorePath)) {
                        add = false;
                    }
                }
                if (add) {
                    scanPaths.add(path);
                }
            } else {
                for (File internalFile : file.listFiles()) {
                    tempList.addLast(internalFile.getAbsolutePath());
                }
            }
        }
        System.out.println("File scanner finished in " + (System.currentTimeMillis() - start) + "ms");
        System.out.println("Found " + scanPaths.size() + "files");

        synchronized (this) {
            this.notifyAll();
        }

    }

    public synchronized File getNextFile() {
        return new File(scanPaths.poll());
    }

    public synchronized boolean hasNext() {
        return (scanPaths.peek() != null);
    }

    public synchronized int getSize() {
        System.out.println("[INFO]Size requested" + scanPaths.size());
        return scanPaths.size();
    }

}