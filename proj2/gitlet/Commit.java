package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;
import static gitlet.Repository.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Huang Jinhong
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** The date of committing. */
    private Date date;
    /** The ids(hash codes) of blobs in this Commit. */
    private List<String> blobIDs = new LinkedList<>();
    /** The ids(hash codes) of parent Commits, BUT at most two parents*/
    private List<String> parentIDs;
    /** The id(hash code) of this Commit. */
    private String CommitID;
    /** The name of copied files in blobs. */
    private List<String> copiedFileNames;
    /** The id(hash code)  of copied files in blobs. */
    private List<String> copiedFileIDs = new LinkedList<>();

    public Commit(String message, Date date, List<String> parentIDs) {
        this.message = message;
        this.date = date;
        this.parentIDs = parentIDs;
        this.copiedFileNames = plainFilenamesIn(STAGING_FOLDER);
        HashSet<File> stagingFiles = getFiles(STAGING_FOLDER);
        if (stagingFiles != null) {
            for (File file : stagingFiles) {
                this.blobIDs.add(makeBlob(file).getBlobID());
            }
            for (File file : stagingFiles) {
                this.copiedFileIDs.add(getFileID(file));
            }
        }
        this.CommitID = sha1(serialize(this));
    }

    // get currentCommitID
    public String getCommitID() {
        return this.CommitID;
    }

    //get parentIDs
    public List<String> getParentIDs() {
        return this.parentIDs;
    }

    // get blobIDs
    public List<String> getBlobIDs() {
        return this.blobIDs;
    }

    // get copiedFileNames
    public List<String> getCopiedFileNames() {
        return this.copiedFileNames;
    }

    // get copiedFileIDs
    public List<String> getCopiedFileIDs() {
        return this.copiedFileIDs;
    }

    // get Date
    public Date getDate() {
        return this.date;
    }
    // get message
    public String getMessage() {
        return this.message;
    }

    public Blob makeBlob(File stagingFile) {
        Blob blob = new Blob(stagingFile);
        String blobID = blob.getBlobID();
        saveDirAndFileInBlobs(blob, BLOB_FOLDER, blobID);
        return blob;
    }

    // get blobs AS HashSet of blobs
    public HashSet<Blob> getBlobs() {
        HashSet<Blob> blobs = new HashSet<>();
        for (String ID : blobIDs) {
            Blob blob = readObject(join(BLOB_FOLDER, getDirID(ID), ID), Blob.class);
            blobs.add(blob);
        }
        return blobs;
    }

    // TODO: using Serializable?
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
}
