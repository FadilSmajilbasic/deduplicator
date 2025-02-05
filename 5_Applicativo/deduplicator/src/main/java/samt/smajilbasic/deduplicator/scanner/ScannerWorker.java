package samt.smajilbasic.deduplicator.scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityExistsException;

import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.repository.FileRepository;

/**
 * Thread for scanning
 */
public class ScannerWorker extends Thread {

    private Report report;
    private FileRepository fileRepository;
    private File file;
    private ScannerWorkerListener listener;
    private final static int BUFFER_SIZE = 32768;

    public ScannerWorker(File file, FileRepository fileRepository, Report report, ScannerWorkerListener listener) {
        this.report = report;
        this.fileRepository = fileRepository;
        this.file = file;
        this.listener = listener;
    }

    @Override
    public void run() {
        if (file.isFile()) {
            try {

                RandomAccessFile fileRAF = new RandomAccessFile(file.getAbsolutePath(), "r");
                String hash = getHash(fileRAF, "MD5");
                long size = fileRAF.length();
                Long lastModified = file.lastModified();
                fileRAF.close();
                samt.smajilbasic.deduplicator.entity.File record = new samt.smajilbasic.deduplicator.entity.File(
                    file.getAbsolutePath(), lastModified, hash, size, report);
                try {
                    fileRepository.save(record);
                } catch (EntityExistsException eee) {
                    Logger.getGlobal().log(Level.SEVERE, "Thread " + this.getName() + " File already exists: " + record.getPath());
                    synchronized (listener) {
                        listener.fileNotSaved();
                    }
                    eee.printStackTrace(System.out);
                }

            } catch (NoSuchAlgorithmException nsae) {
                System.err.println("[ERROR] Thread " + this.getName() + " Unable to hash file: " + nsae.getMessage());

                synchronized (listener) {
                    listener.fileNotSaved();
                }

            } catch (IOException ioe) {
                System.err.println("[ERROR] Thread " + this.getName() + " Unable to read file: " + ioe.getMessage());
                synchronized (listener) {
                    listener.fileNotSaved();
                }

            } catch (NullPointerException npe) {
                System.err.println("[ERROR] Thread " + this.getName() + " Unable to save file: " + npe.getMessage());
                synchronized (listener) {
                    listener.fileNotSaved();
                }
            }
        } else {
            System.err.println("[ERROR] Thread " + this.getName() + " Path set is not file: " + file.getAbsolutePath());
            synchronized (listener) {
                listener.fileNotSaved();
            }
        }
    }

    /**
     * Metodo che restituisce il hash del file che gli viene passato come parametro.
     *
     * @param file il file dal quale verrà generato il hash.
     * @param mode il tipo di hash da generare.
     * @return il hash del cententuto del file in formato stringa.
     * @throws NoSuchAlgorithmException Eccezzione tirata in caso che il tipo di
     *                                  hash non è disponibile.
     */
    public String getHash(RandomAccessFile file, String mode) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(mode);

        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            long read = 0;

            long end = file.length();
            int unitsize;
            while (read < end) {
                unitsize = (int) (((end - read) >= BUFFER_SIZE) ? BUFFER_SIZE : (end - read)); // controllo se sono
                // arrivato in fondo al
                // file.
                file.read(buffer, 0, unitsize); // leggo un chunk del file definito dall'attributo BUFFER_SIZE
                messageDigest.update(buffer, 0, unitsize); // aggiorno il hash con i nuovi dati letti.
                read += unitsize; // sposto il buffer al prossimo chunk di dati.
            }

        } catch (FileNotFoundException fnfE) {
            System.err.println("[ERROR] Thread " + this.getName() + " hash generation: File not found");

        } catch (IOException ioE) {
            System.err.println("[ERROR] Thread " + this.getName() + " hash generation: IO Exception");
        }

        byte[] digest = messageDigest.digest();

        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < digest.length; i++) {
            hexString.append(String.format("%02x", digest[i]));
        }

        return hexString.toString();
    }

}