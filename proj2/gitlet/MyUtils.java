package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class MyUtils {
    // in blobs,
    // we may use same blob so, we need to compare ID to parent
    public static void saveDirAndObjInBlobs(Serializable SerObj, File FOLDER, String ID) {
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

    // set the first and second characters of blob id as name of folder of blobs
    public static String getDirID(String ID) {
        return ID.substring(0, 2);
    }

    public static void saveDir(File FOLDER, String dirID) {
        File dir = join(FOLDER, dirID);
        dir.mkdir();
    }

    // save file by FOLDER, subdirectory ID and current ID
    public static void saveObj(File FOLDER, String dirID, String ID, Serializable SerObj) {
        File file = join(FOLDER, dirID, ID);
        writeObject(file, SerObj);
    }

    // in commits,
    // each commit is only one, so we don't need to compare ID
    // save file by FOLDER
    public static void saveObj(File FOLDER, String name, Serializable SerObj) {
        File file = join(FOLDER, name);
        writeObject(file, SerObj);
    }

    // saveObj vs saveContent
    // Obj: writeObject(file, SerObj);
    // File: writeObject(file, content);
    public static void saveContent(File FOLDER, String name, String content) {
        File file = join(FOLDER, name);
        writeContents(file, content);
    }

    // using with CWD/STAGING/REMOVED/blob/commit
    // get file id by using filename and file content(as string)
    public static String getFileID(File file) {
        return sha1(serialize(file.getName()), serialize(readContentsAsString(file)));
    }

    // validate filesystem
    public static boolean validateDirAndFolder(){
        return GITLET_DIR.exists();
    }
}
