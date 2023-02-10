package Log;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging implements ILogging{
    private File file;
    private FileHandler fileHandler;

    public Logging(String fileName, boolean client, boolean server) {
        this.file = this.createFile(fileName,client,server);
        this.fileHandler = this.setFileHandler();
    }

    private File createFile(String fileName, boolean client, boolean server) {
        final String dir = System.getProperty("user.dir");
        if(client) {
           this.file = new File(dir+"/src/Log/Client/"+fileName+".log");
            if(!this.file.exists()) {
                try {
                    this.file.createNewFile();
                } catch (IOException e) {
                    e.getStackTrace();
                }
            }
            return file;
        } else if(server) {
            this.file = new File(dir+"/src/Log/Server/"+fileName+".log");
            if(!this.file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.getStackTrace();
                }
            }
            return this.file;
        }
        return null;
    }

    private FileHandler setFileHandler() {
        try {
            this.fileHandler = new FileHandler(getFile().getAbsolutePath(),1024*10000,1,true);
            this.fileHandler.setFormatter(new CustomFormatter());
            return this.fileHandler;
        }catch (IOException ex) {
            ex.getStackTrace();
        }
        return this.fileHandler;
    }

    private FileHandler getFileHandlerObj() {
        return this.fileHandler;
    }

    public Logger attachFileHandlerToLogger(Logger logger) {
        logger.addHandler(getFileHandlerObj());
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        return logger;
    }

    private File getFile(){
        return this.file;
    }

}
