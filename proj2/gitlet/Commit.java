package gitlet;

import java.io.File;
import java.io.Serializable;
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
    private List<String> blobIDs;
    /** The ids(hash codes) of parent Commits, BUT at most two parents*/
    private List<String> parentIDs;
    /** The id(hash code) of this Commit. */
    private String CommitID;
    /** The name of copied files in blobs. */
    private List<String> copiedFileNames;
    /** The id(hash code)  of copied files in blobs. */
    private List<String> copiedFileIDs;
    /** the marked count in commit */
    private int markedCount;
    /** the distance in commit */
    private int distance;

    public Commit(String message, Date date, List<String> parentIDs) {
        this.message = message;
        this.date = date;
        this.parentIDs = parentIDs;
        this.copiedFileNames = new LinkedList<>();
        this.blobIDs = new LinkedList<>();
        this.copiedFileIDs = new LinkedList<>();
        // get information(copied fileNames, copied fileIDs and blobIDs) from parent
        getInfoFromParent();
        // get information(copied fileNames, copied fileIDs and blobIDs) from staging folder
        getInfoFromStaging();
        this.markedCount = 0;
        this.distance = 0;
        this.CommitID = sha1(serialize(this));
    }

    /** using in constructor of Commit */
    // make blob
    public Blob makeBlob(File stagingFile) {
        Blob blob = new Blob(stagingFile);
        String blobID = blob.getBlobID();
        saveDirAndObjInBlobs(blob, BLOB_FOLDER, blobID);
        return blob;
    }

    // get information from parent
    // only consider one parent ==> get(0)
    private void getInfoFromParent() {
        // 22.9.28, only consider one parent ==> get(0)
        // get copied fileNames, copied fileIDs and blobIDs by parent
        if (this.parentIDs.size() == 1) {
            Commit parentCommit = readObject(join(COMMITS_FOLDER, this.parentIDs.get(0)), Commit.class);
            this.copiedFileNames.addAll(parentCommit.getCopiedFileNames());
            this.blobIDs.addAll(parentCommit.getBlobIDs());
            this.copiedFileIDs.addAll(parentCommit.getCopiedFileIDs());
        }
    }

    // get information from staging folder and removed folder
    private void getInfoFromStaging() {
        // 1. consider addition folder!
        // get copied fileNames, copied fileIDs and blobIDs by Addition folder
        for (String fileName : plainFilenamesIn(ADDITION_FOLDER)) {
            File file = join(ADDITION_FOLDER, fileName);
            String fileID = getFileID(file);
            // different filename and different fileID, we should add file from staging
            if (!this.copiedFileNames.contains(fileName) && !this.copiedFileIDs.contains(fileID)) {
                this.copiedFileNames.add(fileName);
                this.copiedFileIDs.add(fileID);
                this.blobIDs.add(makeBlob(file).getBlobID());
                // same filename but different fileID, we should use the file from staging to instead of file from parent
                // i.e. deleted file(blobID, copiedFileID) from parent then, add the file from staging
            } else if (this.copiedFileNames.contains(fileName) && !this.copiedFileIDs.contains(fileID)) {
                // deleted file(blobID, copiedFileID) from parent
                for (String blobID : this.blobIDs) {
                    Blob blob = readObject(join(BLOB_FOLDER, getDirID(blobID), blobID), Blob.class);
                    if (blob.getCopiedFileName().equals(fileName)) {
                        this.copiedFileIDs.remove(blob.getCopiedFileID());
                        this.blobIDs.remove(blob.getBlobID());
                        break;
                    }
                }
                // add the file from staging
                this.copiedFileIDs.add(fileID);
                this.blobIDs.add(makeBlob(file).getBlobID());
            }
            // same filename and same fileID, do nothing
        }
        // 2. consider removed folder!
        // remove fileNames, copied fileIDs and blobIDs by Removed folder
        List<String> removedBlobIDs = new LinkedList<>();
        for (String fileName : plainFilenamesIn(REMOVED_FOLDER)) {
            File file = join(REMOVED_FOLDER, fileName);
            String fileID = getFileID(file);
            // same filename and same fileID, remove it from new commit
            if (this.copiedFileNames.contains(fileName) && this.copiedFileIDs.contains(fileID)) {
                this.copiedFileNames.remove(fileName);
                this.copiedFileIDs.remove(fileID);
                // note: you can't delete blobID one by one in this.blobIDs,
                // because you can't change iterator of blobIDs when using iterator of blobIDs
                for (String blobID : this.blobIDs) {
                    Blob blob = readObject(join(BLOB_FOLDER, getDirID(blobID), blobID), Blob.class);
                    if (fileID.equals(blob.getCopiedFileID())) {
                        removedBlobIDs.add(blobID);
                        break;
                    }
                }
            }
        }
        this.blobIDs.removeAll(removedBlobIDs);
    }

    /** using in markBranch() */
    // reset the marked count
    public void resetMarkCount() {
        this.markedCount = 0;
    }

    // updated the marked count
    public void updatedMarkCount() {
        this.markedCount += 1;
    }

    // get the marked count
    public int getMarkCount() {
        return this.markedCount;
    }

    // reset the distance
    public void resetDistance() {
        this.distance = 0;
    }

    // updated the distance
    public void updatedDistance(int distance) {
        this.distance += distance;
    }

    // get the distance
    public int getDistance() {
        return this.distance;
    }

    // get currentCommitID
    public String getCommitID() {
        return this.CommitID;
    }

    /** get variable from commit */
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

    // get blobs AS HashSet of blobs
    public HashSet<Blob> getBlobs() {
        HashSet<Blob> blobs = new HashSet<>();
        for (String ID : blobIDs) {
            Blob blob = readObject(join(BLOB_FOLDER, getDirID(ID), ID), Blob.class);
            blobs.add(blob);
        }
        return blobs;
    }
}
