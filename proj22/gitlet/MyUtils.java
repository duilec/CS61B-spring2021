package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class MyUtils {
    /**
     * .gitlet/ -- top level folder for all persistent data in proj2 folder
     *    - commits/ -- folder containing all of the persistent data for commits
     *    - blobs/ -- folder containing all of the persistent data for blobs
     *    - staging/ -- folder containing all of the temporary data for files of working area
     *                  but the folder of staging is persistent
     */
    public static void saveDirAndFile(Serializable SerObj, File FOLDER, String ID) {
        // if not same DirID, make a dir of commits
        // compared substring(2 chars) to determine create subdir or not
        List<String> dirIDList = Utils.plainFilenamesIn(FOLDER);
        String dirID = getDirID(ID);
        if (!dirIDList.contains(dirID)) {
            saveDir(FOLDER, dirID);
        }
        // if not same ID, make commit in dir(of DirID)
        // compared blobID to determine create blob or not
        //
        List<String> IDList = Utils.plainFilenamesIn(dirID);
        if (IDList.size() == 0 || !IDList.contains(ID)) {
            saveObj(FOLDER, dirID, ID, SerObj);
        }
    }

    // in blobs,
    // we may use same blob so, we need to compare ID to parent
    // todo: NOT compare all blob??? compare REMOVED_FOLDER
    public static void saveDirAndFileInBlobs(Serializable SerObj, File FOLDER, String ID) {
        Commit parentCommit = getCurrentCommit();
        // get blobIDs of parent
        List<String> parentBlobIDs = parentCommit.getBlobIDs();
        if (parentBlobIDs.size() != 0) {
            for (String parentBlobID : parentBlobIDs) {
                // if equaling parentBlobID, directly return, NOT need to create blobFile
                if (ID.equals(parentBlobID)) {
                    return;
                }
            }
        }
        // compared substring(2 chars) to determine create subdir or not
        List<String> dirIDList = Utils.plainFilenamesIn(FOLDER);
        String dirID = getDirID(ID);
        if (!dirIDList.contains(dirID)) {
            saveDir(FOLDER, dirID);
        }
        // compared blobID to determine create blob or not
        List<String> IDList = Utils.plainFilenamesIn(join(FOLDER, dirID));
        if (IDList != null && !IDList.contains(ID)) {
            saveObj(FOLDER, dirID, ID, SerObj);
        }
    }

    public static String getDirID(String ID) {
        return ID.substring(0, 2);
    }

    public static void saveDir(File FOLDER, String dirID) {
        File dir = Utils.join(FOLDER, dirID);
        dir.mkdir();
    }

    // save file by FOLDER, subdirectory ID and current ID
    public static void saveObj(File FOLDER, String dirID, String ID, Serializable SerObj) {
        File file = Utils.join(FOLDER, dirID, ID);
        writeObject(file, SerObj);
    }

    // in commits,
    // NOT create subdirectory (yes!!!)
    // each commit is only one, so we don't need to compare ID
    // save file by FOLDER
    public static void saveObj(File FOLDER, String name, Serializable SerObj) {
        File file = Utils.join(FOLDER, name);
        writeObject(file, SerObj);
    }

    // saveObj vs saveContent
    // Obj: writeObject(file, SerObj);
    // File: writeObject(file, content);
    public static void saveContent(File FOLDER, String name, String content) {
        File file = Utils.join(FOLDER, name);
        writeContents(file, content);
    }

    // get Fake Files by IDList
    public static HashSet<File> getFiles(File FOLDER) {
        List<String> IDList = Utils.plainFilenamesIn(FOLDER);
        if (IDList.size() == 0) {
            return null;
        }
        HashSet<File> files = new HashSet<>();
        for (String ID : IDList) {
            // store actual file by path(Utils.join)
            files.add(join(FOLDER, ID));
        }
        return files;
    }

    // using with CWD/STAGING/REMOVED/blob/commit
    // get file id by using filename and file content(as string)
    public static String getFileID(File file) {
        return sha1(serialize(file.getName()), serialize(readContentsAsString(file)));
    }
}
