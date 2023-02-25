package Log;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface ILogging {
    public Logger attachFileHandlerToLogger(Logger logger);
}
