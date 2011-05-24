package eionet.cr.filestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eionet.cr.config.GeneralConfig;

/**
 * 
 * @author jaanus
 * 
 */
public class FileStore {
    
    /** */
    private static final Logger logger = Logger.getLogger(FileStore.class);

    /** */
    public static final String PATH = GeneralConfig.getRequiredProperty(GeneralConfig.FILESTORE_PATH);

    /** */
    private String userName;

    private File userDir;

    /**
     * 
     * @param userName
     */
    private FileStore(String userName) {

        if (StringUtils.isBlank(userName)) {
            throw new IllegalArgumentException("userName must not be null or blank");
        }

        this.userName = userName;
        userDir = new File(FileStore.PATH, userName);
    }
    
    /**
     * 
     * @param userName
     * @return
     */
    public static FileStore getInstance(String userName){
        return new FileStore(userName);
    }

    /**
     * 
     * @param fileName
     * @param overwrite
     * @param inputStream
     * @return
     * @throws IOException
     */
    public File add(String fileName, boolean overwrite, InputStream inputStream) throws IOException {

        File filePath = prepareFileWrite(fileName, overwrite);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filePath);
            IOUtils.copy(inputStream, outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }

        return null;
    }

    /**
     * 
     * @param fileName
     * @param overwrite
     * @param reader
     * @return
     * @throws IOException
     */
    public File add(String fileName, boolean overwrite, Reader reader) throws IOException {

        File filePath = prepareFileWrite(fileName, overwrite);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filePath);
            IOUtils.copy(reader, outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }

        return null;
    }

    /**
     * 
     * @param fileName
     * @param overwrite
     * @throws FileAlreadyExistsException
     */
    protected File prepareFileWrite(String fileName, boolean overwrite) throws FileAlreadyExistsException {

        if (!userDir.exists() || !userDir.isDirectory()) {
            // creates the directory, including any necessary but nonexistent parent directories
            userDir.mkdirs();
        }

        File filePath = new File(userDir, fileName);
        if (filePath.exists() && filePath.isFile()) {

            if (overwrite == false) {
                throw new FileAlreadyExistsException("File already exists: " + fileName);
            } else {
                filePath.delete();
            }
        }

        return filePath;
    }
    
    /**
     * 
     * @param fileName
     */
    public void delete(String fileName){
        
        File file = new File(userDir, fileName);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }
    
    /**
     * 
     * @param renamings
     */
    public void rename(Map<String,String> renamings){

        int renamedCount = 0;
        if (renamings!=null){
            
            for (Map.Entry<String,String> entry : renamings.entrySet()){
                
                String oldName = entry.getKey();
                String newName = entry.getValue();
                
                File file = new File(userDir, oldName);
                if (file.exists() && file.isFile()) {
                    
                    file.renameTo(new File(userDir, newName));
                    renamedCount++;
                }
            }
        }
        
        logger.debug("Total of " + renamedCount + " files renamed in the file store");
    }

//    /**
//     * 
//     * @param fileSubjectUri
//     * @param fileName
//     * @return
//     */
//    private static final String generateFileName(String fileSubjectUri, String fileName) {
//
//        StringBuilder result = new StringBuilder();
//        if (!StringUtils.isBlank(fileSubjectUri)) {
//            result.append(fileSubjectUri);
//        }
//
//        if (result.length() > 0) {
//            result.append(".");
//        }
//
//        if (!StringUtils.isBlank(fileName)) {
//            result.append(fileName);
//        }
//
//        if (result.length() == 0) {
//            result.append(System.currentTimeMillis());
//        }
//
//        return result.toString();
//    }
}