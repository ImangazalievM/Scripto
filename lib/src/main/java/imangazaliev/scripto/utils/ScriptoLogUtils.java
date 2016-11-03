package imangazaliev.scripto.utils;

import android.util.Log;

import imangazaliev.scripto.ScriptoSettings;

import static imangazaliev.scripto.ScriptoSettings.LogLevel.ERROR;
import static imangazaliev.scripto.ScriptoSettings.LogLevel.FULL;
import static imangazaliev.scripto.ScriptoSettings.LogLevel.INFO;

public class ScriptoLogUtils {

    private static final String LOG_TAG = "Scripto Log";

    public static void logMessage(String message) {
        ScriptoSettings.LogLevel level  = ScriptoSettings.getLogLevel();
        if (level == FULL || level == INFO) {
            Log.d(LOG_TAG, message);
        }
    }

    public static void logError(Throwable t) {
        logError(String.format("Message: %s. Cause: %s", "none", t.getMessage()));
    }

    public static void logError(Throwable t, String message) {
        logError(String.format("Message: %s. Cause: %s", message, t.getMessage()));
    }

    public static void logError(String message) {
        ScriptoSettings.LogLevel level  = ScriptoSettings.getLogLevel();
        if (level == FULL || level == ERROR) {
            Log.d(LOG_TAG, message);
        }
    }

}
