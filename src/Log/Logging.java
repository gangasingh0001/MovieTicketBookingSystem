package Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
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
            createDirectoryIfNotExist(dir,"Client");
            this.file = new File(dir+"/Log/Client/"+fileName+".log");
            if(!this.file.exists()) {
                try {
                    this.file.createNewFile();
                } catch (IOException e) {
                    e.getStackTrace();
                }
            }
            return file;
        } else if(server) {
            createDirectoryIfNotExist(dir,"Server");
            this.file = new File(dir+"/Log/Server/"+fileName+".log");
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

    private void createDirectoryIfNotExist(String dir, String directoryName) {
        try {
            Files.createDirectories(Paths.get(dir+"/Log/"+directoryName));
        }catch (IOException ex) {
            ex.getStackTrace();
        }
    }

    private FileHandler setFileHandler() {
        try {
            this.fileHandler = new FileHandler(this.file.getAbsolutePath(),1024*10000,1,true);
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
        return logger;
    }

}
