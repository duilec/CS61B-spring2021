package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Commit.*;
import static gitlet.MyUtils.*;
import static gitlet.Utils.*;
import static gitlet.Pointer.*;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Huang Jinhong
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The staging area folder. */
    public static final File STAGING_FOLDER = join(GITLET_DIR, "staging");
    /** The commits of directory. */
    public static final File COMMITS_FOLDER = join(GITLET_DIR, "commits");
    /** The blobs and subtrees of directory. */
    public static final File BLOB_FOLDER = join(GITLET_DIR, "blobs");
    /** The removed area folder. */
    public static final File REMOVED_FOLDER = join(GITLET_DIR, "removed");
    /** The branch of folder. */
    public static final File BRANCH_FOLDER = join(GITLET_DIR, "branch");

    /** The name of head */
    public static final String headName = "HEAD";
    /** The name of master(branch) */
    public static final String masterName = "master";

    // after "branch", we will get two parent IDs
    private transient String[] parentID;

    // Such fields will not be serialized, and when back in and deserialized,
    // will be set to their default values (null for reference types).
    // You must be careful when reading the objects that contain transient fields back in
    // to set the transient fields to appropriate values
    private transient Commit parentCommit;
    private transient Commit[] parentCommits;

    // initCommit is fixed
    private transient Commit initCommit;

    // using helperMethod to "init"
    // if success, setup persistence, else print error msg
    public static void initCommand(String msg) {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            // throw Utils.error("A Gitlet version-control system already exists in the current directory.");
        } else {
            setupPersistence(msg);
        }
    }

    // using helperMethod to "add"
    // In gitlet, only one file may be added at a time.
    // todo: repeats add same(one) file, will not thing to happen??
    // todo: repeats add same(one) file after committing this file, will not thing to happen??
    public static void addCommand(String fileName) {
        // if workingFile is empty, print error msg and exit
        if (!hasFileNameInCWD(fileName)) {
            throw Utils.error("File does not exist.");
        }
        // if workingFiles equal files of current commit,
        // NOT adding to staging area and remove it from the staging area if it is already there
        // The file will no longer be staged for removal
        // why removing from the staging area if it is already there?
        // because NOT need to add
        // equal ==> totally equal, NOT only name but also content, so, we should use id(sha-1)
        String fileID = getFileID(join(CWD, fileName));
        for (String copiedFileID : getCurrentCommit().getCopiedFileIDs()) {
            if (fileID.equals(copiedFileID)) {
                // todo: delete join() or readObject(join())???
                // 22.9.24: choose join()
                unrestrictedDelete(join(STAGING_FOLDER, fileName));
            }
        }
        // if workingFiles NOT equal files of current commit
        // get working files then adding to staging area
        File workingFile = join(CWD, fileName);
        String workingFileID = getFileID(workingFile);
        if (!comparedCommitsAndWorking(workingFileID)) {
            saveStagingFile(fileName, readContentsAsString(workingFile));
        }
    }

    public static void commitCommand(String message) {
        if (plainFilenamesIn(STAGING_FOLDER).size() == 0) {
            throw error("No changes added to the commit.");
        }
        // make new commit, then save it
        Commit commit = makeCommitWithoutInit(message);
        saveObj(COMMITS_FOLDER, commit.getCommitID(), commit);
        // clean staging folder
        cleanStaging();
    }

    // Unstage the file if it is currently staged for addition.
    public static void rmCommand(String fileName) {
        // get stagingFileIDs
        List<String> stagingFileNames = plainFilenamesIn(STAGING_FOLDER);
        List<String> stagingFileIDs = new LinkedList<>();
        for (String stagingFileName : stagingFileNames) {
            String stagingFileID = getFileID(join(STAGING_FOLDER, stagingFileName));
            stagingFileIDs.add(stagingFileID);
        }
        // get fileID by fileName
        String fileID = getFileID(join(STAGING_FOLDER, fileName));
        // Unstage the file if it is currently staged for addition.
        for (String stagingFileID : stagingFileIDs) {
            if (fileID.equals(stagingFileID)) {
                unrestrictedDelete(join(STAGING_FOLDER, fileName));
                return;
            }
        }

        // If the file is tracked in the current commit,
        // stage it for removal and
        // remove the file from the working directory if the user has not already done so
        // i.e. the user not already delete the file
        // compared ID or Name? choose ID
        // get current commit
        Commit currentCommit = getCurrentCommit();
        for (String commitFileID : currentCommit.getCopiedFileIDs()) {
            if (fileID.equals(commitFileID)) {
                // todo: delete join() or readObject(join())???
                // 22.9.24: choose join()
                // save(stage it for removal)
                File stagingFile = join(STAGING_FOLDER, fileName);
                saveRemovedFile(fileName, readContentsAsString(stagingFile));
                // delete it in staging
                unrestrictedDelete(stagingFile);
                // delete it in working
                if (hasFileIDInCWD(fileID)) {
                    unrestrictedDelete(fileName);
                }
                return;
            }
        }
        // If the file is neither staged nor tracked by the head commit, print the error message
        throw Utils.error("No reason to remove the file.");
    }

    // the fileID in CWD?
    private static boolean hasFileIDInCWD(String fileID) {
        for (String workingFileName : plainFilenamesIn(CWD)) {
            String workingFileID = getFileIDbyName(CWD, workingFileName);
            if (workingFileID.equals(fileID)) {
                return true;
            }
        }
        return false;
    }

    // the filename in CWD?
    private static boolean hasFileNameInCWD(String fileName) {
        for (String workingFileName : plainFilenamesIn(CWD)) {
            if (workingFileName.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public static void logCommand() {
        printCommitLogInActiveBranch(getCurrentCommit());
    }

    private static void printCommitLogInActiveBranch(Commit commit) {
        if (commit == null) {
            return;
        }
        // 22.9.25, only consider one parent
        List<String> parentIDs = commit.getParentIDs();
        boolean hasParentIDs = (parentIDs != null);
        printCommitLog(commit);
        if (hasParentIDs) {
            Commit parentCommit = readObject(join(COMMITS_FOLDER, parentIDs.get(0)), Commit.class);
            printCommitLogInActiveBranch(parentCommit);
        }
    }

    public static void globalLogCommand() {
        for (String ID : plainFilenamesIn(COMMITS_FOLDER)) {
            Commit commit = readObject(join(COMMITS_FOLDER, ID), Commit.class);
            printCommitLog(commit);
        }
    }

    public static void findCommand(String message) {
        boolean hasCommitWithMessage = false;
        for (String ID : plainFilenamesIn(COMMITS_FOLDER)) {
            Commit commit = readObject(join(COMMITS_FOLDER, ID), Commit.class);
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.getCommitID());
                hasCommitWithMessage = true;
            }
        }
        if (hasCommitWithMessage) {
            throw error("Found no commit with that message.");
        }
    }

    public static void statusCommand() {
        printStatus();
    }

    // 22.9.25, the first function fo checkout: java gitlet.Main checkout -- [file name]
    public static void checkoutCommand(String[] args){
        // checkout [branch name]
        if (args.length == 2) {

        }
        // checkout -- [file name]
        // args[2] ==> [file name]
        if (args.length == 3) {
            // just only consider head(current) commit!!!
            checkoutWith(args[2]);
        }
        // checkout [commit id] -- [file name]
        // args[2] ==> [commit id]; args[4] ==> [file name]
        if (args.length == 4) {
            checkoutWith(args[1], args[3]);
        }
    }

    private static void checkoutWithBranchName(String branchName) {

    }

    private static void checkout(Commit commit, String fileName) {
        // finding filename in currentCommit
        for (Commit.Blob blob : commit.getBlobs()) {
            // if success about finding filename in commit
            if (fileName.equals(blob.getCopiedFileName())) {
                // return the file content of filename
                String content = blob.getCopiedFileContent();
                // change(save) file content in CWD
                saveContent(CWD, fileName, content);
                return;
            }
        }
        throw error("File does not exist in that commit.");
    }

    private static void checkoutWith(String fileName) {
        checkout(getCurrentCommit(), fileName);
    }

    private static void checkoutWith(String commitID, String fileName) {
        Commit commit = readObject(join(COMMITS_FOLDER, commitID), Commit.class);
        checkout(commit, fileName);
    }


    // create a commit, if success, return a commit, else return null
    private static Commit makeCommit(String msg, Boolean isInit) {
        // TODO: two parent?
        // make commit
        Commit commit;
        Date date = getDate(isInit);
        if (isInit) {
            commit = new Commit(msg, date, null);
        } else {
            List<String> parentID = getParentIDs();
            commit = new Commit(msg, date, parentID);
        }

        // save commit
        String currentCommitID = commit.getCommitID();
        saveObj(COMMITS_FOLDER, currentCommitID, commit);
        // saveDirAndFile(commit, COMMITS_FOLDER, currentCommitID);

        // modify current commit ID in active branch
        if (!isInit) {
            // extract HEAD, then get ActiveBranchName
            String activeBranchName = extractHEADThenGetActiveBranchName();
            // modify current commit ID in active branch
            saveActiveBranch(activeBranchName, currentCommitID);
        }
        return commit;
    }

    // get date by "isInit = true OR false"
    private static Date getDate(boolean isInit) {
        if (isInit) {
            return new Date(0);
        } else {
            Date date = new Date();
            return new Date(date.getTime());
        }
    }

    // 22.9.21, just get one parentID, NOT consider Merge(two parent)
    private static List<String> getParentIDs() {
        List<String> parentIDs = new LinkedList<>();

        // extract HEAD, then get ActiveBranchName
        String activeBranchName = extractHEADThenGetActiveBranchName();
        // extract active branch, then get current CommitID AS parentID
        String currentCommitID = extractActiveBranchThenGetCurrentCommitID(activeBranchName);
        // add parentIDs
        parentIDs.add(currentCommitID);
        return parentIDs;
    }

    // get current commit(parent commit)
    public static Commit getCurrentCommit() {
        // get current commit(parent)
        String activeBranchName = extractHEADThenGetActiveBranchName();
        String currentCommitID = extractActiveBranchThenGetCurrentCommitID(activeBranchName);
        Commit currentCommit = readObject(join(COMMITS_FOLDER, currentCommitID), Commit.class);
        return currentCommit;
    }

    public static String extractHEADThenGetActiveBranchName() {
        Pointer HEAD = readObject(join(GITLET_DIR, headName), Pointer.class);
        return HEAD.getActiveBranchName();
    }

    public static String extractActiveBranchThenGetCurrentCommitID(String activeBranchName) {
        Pointer activeBranch = readObject(join(BRANCH_FOLDER, activeBranchName), Pointer.class);
        return activeBranch.getCommitID();
    }

    // save(change) active
    public static void saveActiveBranch(String branchName, String commitID) {
        Pointer branch = new Pointer(false, branchName, commitID);
        branch.saveBranchFile();
    }

    // save(change) HEAD
    public static void saveHEAD(String activeBranchName, String... initCommitID) {
        Pointer HEAD = new Pointer(true, activeBranchName, initCommitID);
        HEAD.saveHEADFile();
    }

    // make Commit without initialization
    public static Commit makeCommitWithoutInit(String msg) {
        return makeCommit(msg, false);
    }

    // make Commit with initialization
    public static Commit makeCommitWithInit(String msg) {
        return makeCommit(msg, true);
    }

    // using in addCommand
    // compared commit files in and working files
    // if equal return true, else return false
    // we only compare name of files
    // NOTE: plainFilenamesIn(): Returns a list of the names of all plain files in the directory DIR,
    // in lexicographic order as Java Strings.
    public static boolean comparedCommitsAndWorking(String workingFileID) {
        Commit currentCommit = getCurrentCommit();
        List<String> currentFileIDs = currentCommit.getCopiedFileIDs();
        // compare filenames
        // compare file ? NOT, because different content of file has different name in sha-1
        for (String currentFileID : currentFileIDs) {
            if (workingFileID.equals(currentFileID)) {
                return true;
            }
        }
        return false;
    }

    // using in log command and global-log command
    private static void printCommitLog(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getCommitID());
        List<String> parentIDs = commit.getParentIDs();
        if (parentIDs != null && parentIDs.size() == 2) {
            System.out.println("Merge: " + parentIDs.get(0).substring(0, 7) + " "
                    + parentIDs.get(1).substring(0, 7));
        }
        // how to get Pacific Standard Time in java?
        // different during date and timestamp:
        // i.e. Timestamp: 2022-09-25 20:52:21.425 vs Date: Wed Dec 31 16:00:00 1969 -0800
        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z");
        System.out.println("Date: " + dateFormat.format(commit.getDate()));
        System.out.println(commit.getMessage());
        System.out.println();
    }

    // using in status command
    // 22.9.26, print status, don't care about "modifications not staged and untracked files"
    private static void printStatus() {
        System.out.println("=== Branches ===");
        String activeBranchName = extractHEADThenGetActiveBranchName();
        for (String branchFileName : plainFilenamesIn(BRANCH_FOLDER)) {
            if (activeBranchName.equals(branchFileName)) {
                System.out.println("*" + branchFileName);
            } else {
                System.out.println(branchFileName);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (String stagingFileName : plainFilenamesIn(STAGING_FOLDER)) {
            System.out.println(stagingFileName);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String removedFileName : plainFilenamesIn(REMOVED_FOLDER)) {
            System.out.println(removedFileName);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ==="  + "\n");
        System.out.println("=== Untracked Files ===" + "\n");
    }

    // using in commit command
    // clean Staging
    public static void cleanStaging() {
        for (String fileName : plainFilenamesIn(STAGING_FOLDER)) {
            unrestrictedDelete(join(STAGING_FOLDER, fileName));
        }
    }

    // using in add command
    // save Staging
    // todo: use fileName or fileID to save?
    // 22.9.25, choose fileName, maybe get fileID lose name?
    // saveObj, because we need create new file
    public static void saveStagingFile(String fileName, String contents) {
        saveContent(STAGING_FOLDER, fileName, contents);
    }

    // clean Removed
    public static void cleanRemoved() {

    }
    // save Removed
    public static void saveRemovedFile(String fileName, String contents) {
        saveContent(REMOVED_FOLDER, fileName, contents);
    }

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     *
     * .gitlet/ -- top level folder for all persistent data in proj2 folder
     *    - commits/ -- folder containing all of the persistent data for commits
     *    - blobs/ -- folder containing all of the persistent data for blobs
     *    - staging/ -- folder containing all of the temporary data for files of working area
     *                  but the folder of staging is persistent
     */
    public static void setupPersistence(String msg) {
        // create filesystem (i.e. create directories and folders)
        if (!GITLET_DIR.exists()){
            GITLET_DIR.mkdir();
        }
        if (!COMMITS_FOLDER.exists()){
            COMMITS_FOLDER.mkdir();
        }
        if (!BLOB_FOLDER.exists()){
            BLOB_FOLDER.mkdir();
        }
        if (!STAGING_FOLDER.exists()){
            STAGING_FOLDER.mkdir();
        }
        if (!REMOVED_FOLDER.exists()){
            REMOVED_FOLDER.mkdir();
        }
        if (!BRANCH_FOLDER.exists()){
            BRANCH_FOLDER.mkdir();
        }

        // create initial commit
        Commit initCommit = makeCommitWithInit(msg);
        String initCommitID= initCommit.getCommitID();

        // create HEAD and master
        saveActiveBranch(masterName, initCommitID);
        saveHEAD(masterName, initCommitID);
    }

    /**
     * If a user inputs a command that requires being
     * in an initialized Gitlet working directory
     * (i.e., one containing a .gitlet subdirectory),
     * but is not in such a directory,
     * print the message Not in an initialized Gitlet directory.
     *
     */
    public static boolean validateDirAndFolder(){
        return GITLET_DIR.exists();
    }
}
