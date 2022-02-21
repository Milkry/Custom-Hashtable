import java.io.FileWriter;
import java.io.IOException;

/**
 * This class logs various information about the hashtable
 *
 * @author Panagiotis Stathpoulos (19064087)
 *
 */

public class Log {
    static String logfile = "HashtableLog.txt";
    static FileWriter file;

    /**
     * Responsible for setting up a new entry for each program session
     *
     * @throws IOException
     */
    public static void logFileSetup() throws IOException {
        try {
            file = new FileWriter(logfile, false);
            file.write("##################################################################################\n");
            file.write("################################## NEW LOG ENTRY #################################\n");
            file.write("##################################################################################\n");
        } catch (IOException ex) {
            System.out.println(" >>> UNKNOWN ERROR OCCURRED. PROCEEDING WITHOUT LOGGING <<< ");
        }
        finally {
            file.close();
        }
    }

    /**
     * Saves various information to a log file
     *
     * @param message what to write in the log file
     * @throws IOException
     */
    public static void saveToFile(String message) throws IOException {
        assert !message.equals("");

        try {
            file = new FileWriter(logfile, true);
            file.write(message);
        }
        catch (IOException ex) {
            System.out.println(" >>> SAVE FAILED <<< ");
        }
        finally {
            file.close();
        }
    }
}
