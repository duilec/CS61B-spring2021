package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.MyUtils.getFileID;
import static gitlet.Utils.*;

public class Blob implements Serializable {
    /** The id(hash code) of blob. */
    private String blobID;
    /** The copied file of blob. */
    private File copiedFile;
    /** The copied file id(hash code) of blob. */
    private String copiedFileID;
    /** The copied file content of blob. */
    private String copiedFileContent;
    /** The copied file name of blob. */
    private String copiedFileName;

    // each blob has one file
    public Blob(File stagingFile) {
        // copiedFile just a soft link
        this.copiedFile = stagingFile;
        this.copiedFileID = getFileID(stagingFile);
        this.blobID = sha1(serialize(this));
        this.copiedFileName = stagingFile.getName();
        this.copiedFileContent = readContentsAsString(stagingFile);
    }

    public String getBlobID() {
        return this.blobID;
    }

    public String getCopiedFileName() {
        return this.copiedFileName;
    }

    public String getCopiedFileContent() {
        return this.copiedFileContent;
    }

    public String getCopiedFileID() {
        return this.copiedFileID;
    }
}
