package gitlet;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

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
    /** The addition area folder. */
    // note: staging folder include addition area and removed area
    public static final File ADDITION_FOLDER = join(GITLET_DIR, "addition");
    /** The removed area folder. */
    public static final File REMOVED_FOLDER = join(GITLET_DIR, "removed");
    /** The commits of directory. */
    public static final File COMMITS_FOLDER = join(GITLET_DIR, "commits");
    /** The blobs and subtrees of directory. */
    public static final File BLOB_FOLDER = join(GITLET_DIR, "blobs");
    /** The branch of folder. */
    public static final File BRANCH_FOLDER = join(GITLET_DIR, "branch");
    /** The remote of folder */
    public static final File REMOTE_FOLDER = join(GITLET_DIR, "remote");
    /** The name of head */
    public static final String headName = "HEAD";
    /** The name of master(branch) */
    public static final String masterName = "master";


    /** based command */
    // using helperMethod to "init"
    // if success, setup persistence, else print error msg
    public static void initCommand(String msg) {
        if (validateDirAndFolder()) {
            printError("A Gitlet version-control system already exists in the current directory.");
        } else {
            setupPersistence(msg);
        }
    }

    // using helperMethod to "add"
    // In gitlet, only one file may be added at a time.
    // NOTE: you should make check after testing
    public static void addCommand(String fileName) {
        // if workingFile is empty, print error msg and exit
        if (!hasFileNameInCWD(fileName)) {
            printErrorWithExit("File does not exist.");
        }
        // if workingFiles equal files of current commit,
        // NOT adding to staging area and remove it from the staging area if it is already there
        // The file will no longer be staged for removal
        // why removing from the staging area if it is already there?
        // because NOT need to add
        // equal ==> totally equal, NOT only name but also content, so, we should use id(sha-1)
        String fileID = getFileID(join(CWD, fileName));
        for (Blob blob : getCurrentCommit().getBlobs()) {
            if (fileID.equals(blob.getCopiedFileID())) {
                unrestrictedDelete(join(REMOVED_FOLDER, fileName));
                unrestrictedDelete(join(ADDITION_FOLDER, fileName));
                return;
            }
        }
        // if workingFiles NOT equal files of current commit
        // get working files then adding to staging area
        File workingFile = join(CWD, fileName);
        String workingFileID = getFileID(workingFile);
        if (!comparedCommitsAndWorking(workingFileID)) {
            saveAdditionFile(fileName, readContentsAsString(workingFile));
        }
    }

    public static void commitCommand(String message) {
        commitCommandHelper(message, false, null);
    }

    // Unstage the file if it is currently staged for addition.
    public static void rmCommand(String fileName) {
        // in staging
        // Unstage the file if it is currently staged for addition.
        if (plainFilenamesIn(ADDITION_FOLDER).contains(fileName)) {
            // get stagingFileIDs
            List<String> stagingFileIDs = new LinkedList<>();
            for (String stagingFileName : plainFilenamesIn(ADDITION_FOLDER)) {
                String stagingFileID = getFileID(join(ADDITION_FOLDER, stagingFileName));
                stagingFileIDs.add(stagingFileID);
            }
            // get fileID if stagingFiles exist
            if (stagingFileIDs.size() != 0) {
                String fileID = getFileID(join(ADDITION_FOLDER, fileName));
                // Unstage the file if it is currently staged for addition.
                for (String stagingFileID : stagingFileIDs) {
                    if (fileID.equals(stagingFileID)) {
                        unrestrictedDelete(join(ADDITION_FOLDER, fileName));
                        return;
                    }
                }
            }
        }
        // in cwd
        // If the file is tracked in the current commit,
        // stage it for removal and
        // remove the file from the working directory if the user has not already done so
        // i.e. the user have not deleted the file
        // get current commit
        Commit currentCommit = getCurrentCommit();
        for (Blob blob : currentCommit.getBlobs()) {
            if (fileName.equals(blob.getCopiedFileName())) {
                // save(stage it for removal)
                saveRemovedFile(fileName, blob.getCopiedFileContent());
                // delete it in staging
                unrestrictedDelete(join(ADDITION_FOLDER, fileName));
                // delete it in working
                if (hasFileNameInCWD(fileName)) {
                    restrictedDelete(join(CWD, fileName));
                }
                // delete it in
                return;
            }
        }
        // If the file is neither staged nor tracked by the head commit, print the error message
        printError("No reason to remove the file.");
    }

    // log command: following the first parent commit links,
    // ignoring any second parents found in merge commits.
    public static void logCommand() {
        printCommitLogInActiveBranch(getCurrentCommit());
    }

    private static void printCommitLogInActiveBranch(Commit commit) {
        if (commit == null) {
            return;
        }
        printCommitLog(commit);
        List<String> parentIDs = commit.getParentIDs();
        if (parentIDs.size() > 0) {
            Commit parentCommit = readObject(join(COMMITS_FOLDER, parentIDs.get(0)), Commit.class);
            printCommitLogInActiveBranch(parentCommit);
        }
    }

    public static void globalLogCommand() {
        // Like log, except displays information about all commits ever made.
        // The order of the commits does not matter.
        // i.e. prints all commits in Commits
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
        if (!hasCommitWithMessage) {
            printError("Found no commit with that message.");
        }
    }

    public static void statusCommand() {
        printStatus();
    }

    // 22.9.25, the first function fo checkout: java gitlet.Main checkout -- [file name]
    public static void checkoutCommand(String[] args){
        // checkout [branch name]
        if (args.length == 2) {
            checkoutWithBranchName(args[1]);
        }
        // checkout -- [file name]
        // args[2] ==> [file name]
        if (args.length == 3) {
            // just only consider head(current) commit!!!
            checkoutWithFileName(args[2]);
        }
        // checkout [commit id] -- [file name]
        // args[2] ==> [commit id]; args[4] ==> [file name]
        if (args.length == 4) {
            checkoutWithCommitIDAndFileName(args[1], args[3]);
        }
    }

    public static void resetCommand(String commitID) {
        // If no commit with the given id exists, print error with exit
        checkNotExistSameFileInFolder(commitID, COMMITS_FOLDER, "No commit with that id exists.");
        Commit commit = readObject(join(COMMITS_FOLDER, commitID), Commit.class);
        checkUntrackedFileError();
        // note, removing at first then, checkout
        // firstly,
        // Removes tracked files that are not present in that commit(the given commit).
        // i.e. remove files in cwd
        for (String workingFileName : plainFilenamesIn(CWD)) {
            if (!commit.getCopiedFileIDs().contains(getFileID(join(CWD, workingFileName)))) {
                restrictedDelete(join(CWD, workingFileName));
            }
        }
        // secondly,
        // Checks out all the files tracked by the given commit.
        // check out i.e. find it in commits folder, then put it in CWD
        for (String copiedFileName : commit.getCopiedFileNames()) {
            checkout(commit, copiedFileName);
        }
        // Also moves the current branch’s head to that commit node.
        saveActiveBranch(extractHEADThenGetActiveBranchName(), commitID);
        // The staging(removed, addition) area is cleared.
        cleanStaging();
    }

    public static void branchCommand(String branchName) {
        //  If a branch with the given name already exists, print the error message
        checkExistSameFileInFolder(branchName, BRANCH_FOLDER, "A branch with that name already exists.");
        saveBranch(branchName, getCurrentCommit().getCommitID());
    }

    public static void rmBranchCommand(String branchName) {
        // check the branch not exits
        checkNotExistSameFileInFolder(branchName, BRANCH_FOLDER, "A branch with that name does not exist.");
        // check the branch is current branch, it can't be removed
        if (extractHEADThenGetActiveBranchName().equals(branchName)) {
            printErrorWithExit("Cannot remove the current branch.");
        }
        unrestrictedDelete(join(BRANCH_FOLDER, branchName));
    }

    public static void mergeCommand(String branchName) {
        // it is remote branch?
        branchName = convertRemoteBranchName(branchName);
        // If there are staged additions or removals present, print the error message and exit.
        if (plainFilenamesIn(ADDITION_FOLDER).size() > 0 || plainFilenamesIn(REMOVED_FOLDER).size() > 0) {
            printErrorWithExit("You have uncommitted changes.");
        }
        // If a branch with the given name does not exist, print the error message and exit.
        if (!plainFilenamesIn(BRANCH_FOLDER).contains(branchName)) {
            printErrorWithExit("A branch with that name does not exist.");
        }
        // If attempting to merge a branch with itself, print the error message and exit.
        if (extractHEADThenGetActiveBranchName().equals(branchName)) {
            printErrorWithExit("Cannot merge a branch with itself.");
        }
        // If merge would generate an error because the commit that it does has no changes in it, just let the normal commit error message for this go through.
        // i.e. print error in commit

        // If an untracked file in the current commit would be overwritten or deleted by the merge, print message and exit
        checkUntrackedFileError();
        // get split commit
        Commit split = getSplitCommit(branchName);
        String splitCommitID = split.getCommitID();
        // If the split point is the same commit as the given branch, print error and exit
        if (splitCommitID.equals(extractBranchThenGetCommitID(branchName))) {
            printErrorWithExit("Given branch is an ancestor of the current branch.");
        }
        // If the split point is the current branch, then the effect is to check out the given branch,
        // and the operation ends after printing the message
        if (splitCommitID.equals(getCurrentCommit().getCommitID())){
            checkoutWithBranchName(branchName);
            printErrorWithExit("Current branch fast-forwarded.");
        }
        // get the target branch commit AS "other"
        String otherCommitID = extractBranchThenGetCommitID(branchName);
        Commit other = readObject(join(COMMITS_FOLDER, otherCommitID), Commit.class);
        merge(split, other);
        commitCommandHelper(getMergeMessage(branchName), true, branchName);
    }

    /** remote command */
    public static void addRemoteCommand(String remoteName, String dirPathString) {
        // If a remote with the given name already exists, print the error message and exit
        if (plainFilenamesIn(REMOTE_FOLDER).contains(remoteName)) {
            printErrorWithExit("A remote with that name already exists.");
        }
        Remote remote = new Remote(remoteName, dirPathString);
        saveObj(REMOTE_FOLDER, remoteName, remote);
    }

    public static void rmRemoteCommand(String remoteName) {
        // If a remote with the given name already exists, print the error message and exit
        if (!plainFilenamesIn(REMOTE_FOLDER).contains(remoteName)) {
            printErrorWithExit("A remote with that name does not exist.");
        }
        // only delete obj of remote but not delete remote branch, remote blobs and remote commits
        for (String name : plainFilenamesIn(REMOTE_FOLDER)) {
            if (name.equals(remoteName)) {
                unrestrictedDelete(join(REMOTE_FOLDER, name));
            }
        }
    }

    public static void pushCommand(String remoteName, String remoteBranchName) {
        Remote remote = readObject(join(REMOTE_FOLDER, remoteName), Remote.class);
        validateRemoteDir(remote);
        File remoteDir = remote.getRemoteDir();
        // If the remote branch’s head is not in the history of the current local head, print the error message and exit
        Pointer remoteActiveBranch = readObject(join(remoteDir, "branch", remoteBranchName), Pointer.class);
        // note: HEAD point to branch, branch point to commit
        String remoteHeadID = remoteActiveBranch.getCommitID();
        if (!isRemoteHeadIDInHistoryOfLocal(remoteHeadID, getCurrentCommit())) {
            printErrorWithExit("Please pull down remote changes before pushing.");
        }
        // If the remote .gitlet directory does not exist, print the error message and exit
        validateRemoteDir(remote);
        Commit localCurrentCommit = getCurrentCommit();
        push(remoteDir, localCurrentCommit, remoteHeadID);
        saveRemoteBranch(remoteDir, remoteBranchName, localCurrentCommit.getCommitID());
        saveRemoteHEAD(remoteDir, remoteBranchName, getInitCommitID());
    }

    public static void fetchCommand(String remoteName, String remoteBranchName) {
        Remote remote = readObject(join(REMOTE_FOLDER, remoteName), Remote.class);
        // If the remote .gitlet directory does not exist, print the error message and exit
        validateRemoteDir(remote);
        // If the remote Gitlet repository does not have the given branch name, print the error message and exit
        if (!remote.getBranchNames().contains(remoteBranchName)) {
            printErrorWithExit("That remote does not have that branch.");
        }
        File remoteDir = remote.getRemoteDir();
        Pointer branch = readObject(join(remoteDir, "branch", remoteBranchName), Pointer.class);
        Commit commit = readObject(join(remoteDir, "commits", branch.getCommitID()), Commit.class);
        fetch(remoteDir, commit);
        // let remote branch name with backslash
        // eg. R1, master => R1\master
        saveBranch(remoteName + "\\" + remoteBranchName, branch.getCommitID());
    }

    public static void pullCommand(String remoteName, String remoteBranchName) {
        fetchCommand(remoteName, remoteBranchName);
        mergeCommand(remoteName + "/" +remoteBranchName);
    }

    /** base */
    /** using in checkout command */
    private static void checkoutWithFileName(String fileName) {
        checkout(getCurrentCommit(), fileName);
    }

    private static void checkoutWithBranchName(String branchName) {
        // it is remote branch?
        branchName = convertRemoteBranchName(branchName);
        // If no branch with that name exists
        List<String> branchNames = plainFilenamesIn(BRANCH_FOLDER);
        if (!branchNames.contains(branchName)) {
            printErrorWithExit("No such branch exists.");
        }
        // If that branch is the current branch
        if (extractHEADThenGetActiveBranchName().equals(branchName)) {
            printErrorWithExit("No need to checkout the current branch.");
        }
        // get commit
        String commitID = readObject(join(BRANCH_FOLDER, branchName), Pointer.class).getCommitID();
        Commit branchCommit = readObject(join(COMMITS_FOLDER, commitID), Commit.class);
        //  If a working file is untracked in the current branch and would be overwritten by the checkout
        checkUntrackedFileError();
        // Takes all files in the commit at the head of the given branch,
        // and puts them in the working directory,
        // overwriting the versions of the files that are already there if they exist.
        // firstly, deleted all files in CWD
        for (String name : plainFilenamesIn(CWD)) {
            restrictedDelete(name);
        }
        // secondly, checkout all copiedFile in commit
        for (String copiedFileName : branchCommit.getCopiedFileNames()) {
            checkout(branchCommit, copiedFileName);
        }
        // Also, at the end of this command, the given branch will now be considered the current branch (HEAD)
        saveHEAD(branchName, getInitCommitID());
    }

    private static void checkoutWithCommitIDAndFileName(String commitID, String fileName) {
        // get commitID by short(abbreviated) id
        if (commitID.length() == 8) {
            for (String ID : plainFilenamesIn(COMMITS_FOLDER)) {
                if (ID.substring(0, 8).equals(commitID)) {
                    commitID = ID;
                    break;
                }
            }
        }
        // check "No commit with that id exists."
        checkNotExistSameFileInFolder(commitID, COMMITS_FOLDER, "No commit with that id exists.");
        Commit commit = readObject(join(COMMITS_FOLDER, commitID), Commit.class);
        // check "File does not exist in that commit."
        checkNotExistSameFileInCommit(fileName, commit, "File does not exist in that commit.");
        checkout(commit, fileName);
    }

    // check out i.e. find it in commits folder, then put it in CWD
    private static void checkout(Commit commit, String fileName) {
        // finding filename in commit
        for (Blob blob : commit.getBlobs()) {
            // if success about finding filename in commit
            if (fileName.equals(blob.getCopiedFileName())) {
                // change(save) file content in CWD
                String content = blob.getCopiedFileContent();
                saveContent(CWD, fileName, content);
                return;
            }
        }
        printError("File does not exist in that commit.");
    }

    // change remote branch name with backslash but not slash
    // eg. R1/master => R1\master
    private static String convertRemoteBranchName(String branchName) {
        if (branchName.contains("/")) {
            int indexOfslash = branchName.indexOf("/");
            branchName = branchName.substring(0, indexOfslash) + "\\" + branchName.substring(indexOfslash + 1);
        }
        return branchName;
    }

    /** using in merge command */
    private static Commit getSplitCommit(String branchName) {
        // mark the current(head) branch
        Commit headCommit = getCurrentCommit();
        markBranch(headCommit, 0);
        // get the other branch commit then, mark the other branch
        String otherCommitID = extractBranchThenGetCommitID(branchName);
        Commit otherCommit = readObject(join(COMMITS_FOLDER, otherCommitID), Commit.class);
        markBranch(otherCommit, 0);
        // get init commit distance as smallest split commit
        Commit splitCommit = readObject(join(COMMITS_FOLDER, getInitCommitID()), Commit.class);
        int splitDistance = splitCommit.getDistance();
        for (String commitID : plainFilenamesIn(COMMITS_FOLDER)) {
            Commit commit = readObject(join(COMMITS_FOLDER, commitID), Commit.class);
            if (commit.getMarkCount() == 2 && commit.getDistance() < splitDistance) {
                splitCommit = commit;
                splitDistance = commit.getDistance();
            }
        }
        // reset marked count in all commits
        for (String commitID : plainFilenamesIn(COMMITS_FOLDER)) {
            Commit commit = readObject(join(COMMITS_FOLDER, commitID), Commit.class);
            commit.resetMarkCount();
            commit.resetDistance();
            saveObj(COMMITS_FOLDER, commit.getCommitID(), commit);
        }
        return splitCommit;
    }

    // mark branch both first and second parent
    private static void markBranch(Commit branchCommit, int distance) {
        // update marked count and distance
        branchCommit.updatedMarkCount();
        branchCommit.updatedDistance(distance);
        saveObj(COMMITS_FOLDER, branchCommit.getCommitID(), branchCommit);
        // iterate first/second parent commit, we should mark it
        // note: distance need to plus 1
        distance += 1;
        for (String parentID : branchCommit.getParentIDs()) {
            branchCommit = readObject(join(COMMITS_FOLDER, parentID), Commit.class);
            markBranch(branchCommit, distance);
        }
    }

    public static void merge(Commit split, Commit other){
        Commit head = getCurrentCommit();
        Set<String> allFileNames = getAllFileNames(split, head, other);
        Set<String> allFileIDs = getAllFileNames(split, head, other);
        // using 8 rules to store some files to cwd, addition folder and removed folder
        rulesDealFiles(split, head, other, allFileNames);
    }

    private static void rulesDealFiles(Commit split, Commit head, Commit other, Set<String> allFileNames) {
        List<String> fileNamesInSplit = split.getCopiedFileNames();
        List<String> fileNamesInHead = head.getCopiedFileNames();
        List<String> fileNamesInOther = other.getCopiedFileNames();
        // using rules to deal files
        for (String fileName : allFileNames) {
            String fileIDInHead = null;
            String fileIDInOther = null;
            String fileIDInSplit = null;
            Blob headBlob = null;
            Blob otherBlob = null;
            // get head blob, file id and other blob, file id
            for (Blob blob : head.getBlobs()) {
                if (blob.getCopiedFileName().equals(fileName)) {
                    headBlob = blob;
                    fileIDInHead = blob.getCopiedFileID();
                    break;
                }
            }
            for (Blob blob : other.getBlobs()) {
                if (blob.getCopiedFileName().equals(fileName)) {
                    otherBlob = blob;
                    fileIDInOther = blob.getCopiedFileID();
                    break;
                }
            }
            for (Blob blob : split.getBlobs()) {
                if (blob.getCopiedFileName().equals(fileName)) {
                    fileIDInSplit = blob.getCopiedFileID();
                    break;
                }
            }
            // if head not modified "isHeadModified" keep "false"(i.e.head has special fileID)
            // else set "isHeadModified" to "true"(i.e.head NOT special fileID), "isOtherModified" do too
            boolean isHeadModified = (fileIDInHead != null) && (!fileIDInHead.equals(fileIDInSplit));
            boolean isOtherModified = (fileIDInOther != null) && (!fileIDInOther.equals(fileIDInSplit));
            // split, head and other contain a file with same filename
            if (fileNamesInSplit.contains(fileName) && fileNamesInHead.contains(fileName) && fileNamesInOther.contains(fileName)) {
                // 1. modified in other but not head => file from other to staged for addition and put it to cwd
                if (isOtherModified && !isHeadModified) {
                    saveWorkingFile(fileName, otherBlob.getCopiedFileContent());
                    saveAdditionFile(fileName, otherBlob.getCopiedFileContent());
                    continue;
                }
                // 2. modified in head but not other => file from head to staged for addition and put it to cwd
                if (isHeadModified && !isOtherModified) {
                    saveWorkingFile(fileName, headBlob.getCopiedFileContent());
                    saveAdditionFile(fileName, headBlob.getCopiedFileContent());
                    continue;
                }
            }
            // split and other NOT contain a file with same filename but head contain
            // 5. not in split nor other but in head => file from head to staged for addition
            if (!fileNamesInSplit.contains(fileName) && !fileNamesInOther.contains(fileName) && fileNamesInHead.contains(fileName)) {
                saveWorkingFile(fileName, headBlob.getCopiedFileContent());
                saveAdditionFile(fileName, headBlob.getCopiedFileContent());
                continue;
            }
            // split and head NOT contain a file with same filename but other contain
            // 6. not in split nor head but in other => file from other to staged for addition
            if (!fileNamesInSplit.contains(fileName) && !fileNamesInHead.contains(fileName) && fileNamesInOther.contains(fileName)) {
                saveWorkingFile(fileName, otherBlob.getCopiedFileContent());
                saveAdditionFile(fileName, otherBlob.getCopiedFileContent());
                continue;
            }
            // 7a. unmodified in head but not present in other => file from head to staged for removed
            // 7b. modified in head but not present in other => conflict
            if (fileNamesInSplit.contains(fileName) && fileNamesInHead.contains(fileName) && !fileNamesInOther.contains(fileName)) {
                if (!isHeadModified) {
                    saveRemovedFile(fileName, headBlob.getCopiedFileContent());
                    // delete it in working
                    if (hasFileNameInCWD(fileName)) {
                        restrictedDelete(join(CWD, fileName));
                    }
                } else {
                    makeConflictFile(fileName, headBlob.getCopiedFileContent(), "");
                }
                continue;
            }
            // 8a. unmodified in other but not present in head => file from other to staged for removed
            // 8b. modified in other but not present in head => conflict
            if (fileNamesInSplit.contains(fileName) && fileNamesInOther.contains(fileName) && !fileNamesInHead.contains(fileName)) {
                if (!isOtherModified) {
                    saveRemovedFile(fileName, otherBlob.getCopiedFileContent());
                    // delete it in working
                    if (hasFileNameInCWD(fileName)) {
                        restrictedDelete(join(CWD, fileName));
                    }
                } else {
                    makeConflictFile(fileName, "", otherBlob.getCopiedFileContent());
                }
                continue;
            }
            // conflict deal
            if (fileNamesInOther.contains(fileName) && fileNamesInHead.contains(fileName)) {
                //  modified in head and other in same/different way
                if (isOtherModified && isHeadModified) {
                    // 3. modified in head and other in same way => file from head/other to staged for addition and put it to cwd
                    if (fileIDInHead.equals(fileIDInOther)) {
                        saveWorkingFile(fileName, headBlob.getCopiedFileContent());
                        saveAdditionFile(fileName, headBlob.getCopiedFileContent());
                    // 4. modified in head and other in different way => store conflict file to cwd and addition folder
                    } else {
                        makeConflictFile(fileName, headBlob.getCopiedFileContent(), otherBlob.getCopiedFileContent());
                    }
                }
            }
        }
    }

    // get all filenames
    private static Set<String> getAllFileNames(Commit split, Commit head, Commit other) {
        Set<String> fileNames = new HashSet<>();
        for (String fileName : split.getCopiedFileNames()) {
            fileNames.add(fileName);
        }
        for (String fileName : head.getCopiedFileNames()) {
            fileNames.add(fileName);
        }
        for (String fileName : other.getCopiedFileNames()) {
            fileNames.add(fileName);
        }
        return fileNames;
    }

    private static void makeConflictFile(String fileName, String fileContentsFromHead, String fileContentsFromOther) {
        // you can't use set String contents = null
        // you can't use method of concat()
        String contents = "<<<<<<< HEAD" + "\n"
                          + fileContentsFromHead
                          + "=======" + "\n"
                          + fileContentsFromOther
                          +">>>>>>>" + "\n";
        saveWorkingFile(fileName, contents);
        saveAdditionFile(fileName, contents);
        printError("Encountered a merge conflict.");
    }

    // change remote branch name with slash but not backslash
    // eg. R1\master => R1/master
    private static String deConvertRemoteBranchName(String branchName) {
        if (branchName.contains("\\")) {
            int indexOfBackslash = branchName.indexOf("\\");
            branchName = branchName.substring(0, indexOfBackslash) + "/" + branchName.substring(indexOfBackslash + 1);
        }
        return branchName;
    }

    private static String getMergeMessage(String branchName) {
        branchName = deConvertRemoteBranchName(branchName);
        String mergeMessage ="Merged " + branchName + " into "+ extractHEADThenGetActiveBranchName() + ".";
        return mergeMessage;
    }

    /** using in add command */
    // compared commit files and working files
    // if equal return true, else return false
    // we only compare name of files
    // NOTE: plainFilenamesIn(): Returns a list of the names of all plain files in the directory DIR,
    // in lexicographic order as Java Strings.
    public static boolean comparedCommitsAndWorking(String workingFileID) {
        // compare fileIDs
        // compare file ? NOT, because different content of file has different name in sha-1
        for (Blob blob : getCurrentCommit().getBlobs()) {
            if (workingFileID.equals(blob.getCopiedFileID())) {
                return true;
            }
        }
        return false;
    }

    /** using in log command and global-log command */
    // log command: following the first parent commit links,
    // ignoring any second parents found in merge commits.
    private static void printCommitLog(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getCommitID());
        List<String> parentIDs = commit.getParentIDs();
        if (parentIDs.size() == 2) {
            System.out.println("Merge: " + parentIDs.get(0).substring(0, 7) + " "
                                + parentIDs.get(1).substring(0, 7));
        }
        // how to get Pacific Standard Time in java?
        // different during date and timestamp:
        // i.e. Timestamp: 2022-09-25 20:52:21.425 diff. Date: Wed Dec 31 16:00:00 1969 -0800
        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z");
        System.out.println("Date: " + dateFormat.format(commit.getDate()));
        System.out.println(commit.getMessage());
        System.out.println();
    }

    /** using in status command */
    // using in status command
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
        for (String stagingFileName : plainFilenamesIn(ADDITION_FOLDER)) {
            System.out.println(stagingFileName);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String removedFileName : plainFilenamesIn(REMOVED_FOLDER)) {
            System.out.println(removedFileName);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        printModificationsNotStagedForCommit();
        System.out.println();
        System.out.println("=== Untracked Files ===");
        printUntrackedFiles();
        System.out.println();
    }

    private static void printModificationsNotStagedForCommit() {
        Commit currentCommit = getCurrentCommit();
        // init to "false"
        boolean trackedCurrentCommit = false;
        boolean stagedForAddition = false;
        // init to "true"
        boolean changedFromCommit = true;
        boolean changedFromAddition = true;
        for (String workingFileName : plainFilenamesIn(CWD)) {
            String workingFileID = getFileID(join(CWD, workingFileName));
            // Tracked in the current commit, changed in the working directory, but not staged;
            for (Blob blob : currentCommit.getBlobs()) {
                if (blob.getCopiedFileName().equals(workingFileName)) {
                    trackedCurrentCommit = true;
                    if (blob.getCopiedFileID().equals(workingFileID)) {
                        changedFromCommit = false;
                    }
                    break;
                }
            }
            // Staged for addition, but with different contents than in the working directory;
            for (String fileName : plainFilenamesIn(ADDITION_FOLDER)) {
                if (fileName.equals(workingFileName)) {
                    stagedForAddition = true;
                    String fileID = getFileID(join(ADDITION_FOLDER, fileName));
                    if (fileID.equals(workingFileID)) {
                        changedFromAddition = false;
                    }
                    break;
                }
            }
            if (trackedCurrentCommit && changedFromCommit && !stagedForAddition) {
                System.out.println(workingFileName + " (modified)");
            }
            if (stagedForAddition && changedFromAddition) {
                System.out.println(workingFileName + " (modified)");
            }
        }
        // Staged for addition, but deleted in the working directory;
        for (String fileName : plainFilenamesIn(ADDITION_FOLDER)) {
            if (!plainFilenamesIn(CWD).contains(fileName)) {
                System.out.println(fileName + " (deleted)");
            }
        }
        // Not staged for removal, but tracked in the current commit and deleted from the working directory.
        for (Blob blob : currentCommit.getBlobs()) {
            String blobCopiedFileName = blob.getCopiedFileName();
            if (!plainFilenamesIn(REMOVED_FOLDER).contains(blobCopiedFileName)
                    && !plainFilenamesIn(CWD).contains(blobCopiedFileName)) {
                System.out.println(blobCopiedFileName + " (deleted)");
            }
        }
    }

    private static void printUntrackedFiles() {
        // The final category (“Untracked Files”) is for files present in the working directory
        // but neither staged for addition nor tracked.
        boolean isSameName = false;
        for (String workingFileName : plainFilenamesIn(CWD)) {
            // 1. compare in current commit
            for (String fileName : getCurrentCommit().getCopiedFileNames()) {
                if (fileName.equals(workingFileName)) {
                    isSameName = true;
                }
            }
            // 2. compare in addition
            if (plainFilenamesIn(ADDITION_FOLDER).contains(workingFileName)) {
                isSameName = true;
            }
            // neither staged for addition nor tracked.
            if (!isSameName) {
                System.out.println(workingFileName);
            }
        }
    }

    /** using in rm and merge command */
    // the filename in CWD?
    private static boolean hasFileNameInCWD(String fileName) {
        for (String workingFileName : plainFilenamesIn(CWD)) {
            if (workingFileName.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    /** using in commit and merge command */
    private static void commitCommandHelper(String message, boolean afterMerge, String branchName) {
        // The rm command will remove such files, as well as staging them for removal,
        // so that they will be untracked after a commit.
        if (plainFilenamesIn(ADDITION_FOLDER).size() == 0 && plainFilenamesIn(REMOVED_FOLDER).size() == 0) {
            printErrorWithExit("No changes added to the commit.");
        }
        // make new commit, then save it
        if (afterMerge) {
            makeCommitAfterMerge(message, branchName);
        } else {
            makeCommitWithoutInit(message);
        }
        // clean staging folder
        cleanStaging();
    }

    /** using in commit command */
    // make Commit without initialization
    public static Commit makeCommitWithoutInit(String msg) {
        return makeCommit(msg, false, null);
    }

    // make Commit with initialization
    public static Commit makeCommitWithInit(String msg) {
        return makeCommit(msg, true, null);
    }

    // make Commit after merge
    public static Commit makeCommitAfterMerge(String msg, String branchName) {
        return makeCommit(msg, false, branchName);
    }

    // create a commit, if success, return a commit, else return null
    private static Commit makeCommit(String msg, boolean isInit, String otherBranchName) {
        // make commit
        Commit commit;
        Date date = getDate(isInit);
        if (isInit) {
            commit = new Commit(msg, date, new LinkedList<>());
        } else {
            List<String> parentID;
            if (otherBranchName == null) {
                // only has one parent
                parentID = getFirstParentID();
            } else {
                // two parent
                parentID = getTwoParentIDs(otherBranchName);
            }
            commit = new Commit(msg, date, parentID);
        }

        // save commit
        String CommitID = commit.getCommitID();
        saveObj(COMMITS_FOLDER, CommitID, commit);

        // if not initialization, modify current commit ID in active branch
        if (!isInit) {
            // extract HEAD, then get ActiveBranchName
            String activeBranchName = extractHEADThenGetActiveBranchName();
            // modify current commit ID in active branch
            saveActiveBranch(activeBranchName, CommitID);
        }
        return commit;
    }

    // get date by "isInit = true OR false"
    private static Date getDate(boolean isInit) {
        if (isInit) {
            return new Date(0); // get the epoch time
        } else {
            Date date = new Date();
            return new Date(date.getTime());
        }
    }

    private static List<String> getFirstParentID() {
        List<String> parentIDs = new LinkedList<>();
        // get current CommitID AS parentID
        String currentCommitID = getCurrentCommit().getCommitID();
        parentIDs.add(currentCommitID);
        return parentIDs;
    }

    // get two parentIDs
    private static List<String> getTwoParentIDs(String otherBranchName) {
        List<String> parentIDs = new LinkedList<>();
        // get current CommitID AS parentID
        String currentCommitID = getCurrentCommit().getCommitID();
        parentIDs.add(currentCommitID);
        // get CommitID in other branch AS parentID
        String otherBranchCommitID = extractBranchThenGetCommitID(otherBranchName);
        parentIDs.add(otherBranchCommitID);
        return parentIDs;
    }

    // clean Staging of addition and removed
    public static void cleanStaging() {
        for (String fileName : plainFilenamesIn(ADDITION_FOLDER)) {
            unrestrictedDelete(join(ADDITION_FOLDER, fileName));
        }
        for (String fileName : plainFilenamesIn(REMOVED_FOLDER)) {
            unrestrictedDelete(join(REMOVED_FOLDER, fileName));
        }
    }

    /** check some error */
    // check the same file not exists in folder
    private static void checkNotExistSameFileInFolder(String fileName, File FOLDER, String message) {
        boolean existSameFile = false;
        for (String name : plainFilenamesIn(FOLDER)) {
            if (name.equals(fileName)) {
                existSameFile = true;
                break;
            }
        }
        // the same file not exists in folder, so print error and exit
        if (!existSameFile) {
            printErrorWithExit(message);
        }
    }

    // check the same file exists in folder
    private static void checkExistSameFileInFolder(String fileName, File FOLDER, String message) {
        for (String name : plainFilenamesIn(FOLDER)) {
            if (name.equals(fileName)) {
                // the same file exists in folder, so print error and exit
                printErrorWithExit(message);
            }
        }
    }

    // check not same file exists in commit
    private static void checkNotExistSameFileInCommit(String fileName, Commit commit, String message) {
        boolean fileExist = false;
        for (String name : commit.getCopiedFileNames()) {
            if (fileName.equals(name)) {
                fileExist = true;
                break;
            }
        }
        // the same file not exists in commit, so print error and exit
        if (!fileExist) {
            printErrorWithExit(message);
        }
    }

    // check untracked file
    private static void checkUntrackedFileError() {
        // If a working file is untracked in the current branch and would be overwritten by the merge/reset/checkout
        // i.e. working file has same name with tracked file in the current branch(NOT the given commit)
        // but NOT has same contents(aka ID)
        // so, print error with exit
        // note: it is current commit in current branch NOT commit in target branch
        // note: The commit is said to be tracking the saved files
        List<String> copiedFileIDs = getCurrentCommit().getCopiedFileIDs();
        List<String> additionFileIDs = new LinkedList<>();
        for (String fileName : plainFilenamesIn(ADDITION_FOLDER)) {
            additionFileIDs.add(getFileID(join(ADDITION_FOLDER, fileName)));
        }
        for (String workingFileName : plainFilenamesIn(CWD)) {
            String workingFileID = getFileID(join(CWD, workingFileName));
            // if commit and addition both not contain working filename
            if (!copiedFileIDs.contains(workingFileID) && !additionFileIDs.contains(workingFileID)) {
                printErrorWithExit("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
    }

    /** other */
    // save in Addition
    public static void saveAdditionFile(String fileName, String contents) {
        saveContent(ADDITION_FOLDER, fileName, contents);
    }

    // save Removed
    public static void saveRemovedFile(String fileName, String contents) {
        saveContent(REMOVED_FOLDER, fileName, contents);
    }

    // save Working
    public static void saveWorkingFile(String fileName, String contents) {
        saveContent(CWD, fileName, contents);
    }

    // get current commit(parent commit)
    public static Commit getCurrentCommit() {
        // get current commit(parent)
        String activeBranchName = extractHEADThenGetActiveBranchName();
        String currentCommitID = extractActiveBranchThenGetCurrentCommitID(activeBranchName);
        Commit currentCommit = readObject(join(COMMITS_FOLDER, currentCommitID), Commit.class);
        return currentCommit;
    }

    // get initCommit ID, NOTE: initCommit ID never change
    public static String getInitCommitID () {
        Pointer HEAD = readObject(join(GITLET_DIR, headName), Pointer.class);
        return HEAD.getInitCommitID();
    }

    public static String extractHEADThenGetActiveBranchName() {
        Pointer HEAD = readObject(join(GITLET_DIR, headName), Pointer.class);
        return HEAD.getActiveBranchName();
    }

    public static String extractActiveBranchThenGetCurrentCommitID(String activeBranchName) {
        Pointer activeBranch = readObject(join(BRANCH_FOLDER, activeBranchName), Pointer.class);
        return activeBranch.getCommitID();
    }

    public static String extractBranchThenGetCommitID(String BranchName) {
        Pointer branch = readObject(join(BRANCH_FOLDER, BranchName), Pointer.class);
        return branch.getCommitID();
    }

    // save(change) active branch
    public static void saveActiveBranch(String branchName, String commitID) {
        saveBranch(branchName, commitID);
    }

    // save(change) a branch
    public static void saveBranch(String branchName, String commitID) {
        Pointer branch = new Pointer(false, branchName, commitID);
        branch.saveBranchFile();
    }

    // save(change) HEAD
    // we always store initCommitID in HEAD
    public static void saveHEAD(String activeBranchName, String initCommitID) {
        Pointer HEAD = new Pointer(true, activeBranchName, initCommitID);
        HEAD.saveHEADFile();
    }

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     *
     * .gitlet/ -- top level folder for all persistent data in proj2 folder
     *    - commits/ -- folder containing all of the persistent object for commits
     *    - blobs/ -- folder containing all of the persistent folder for folder of blobs
     *          - 00/ -- folders containing all of the persistent object for blobs
     *          - 01/
     *          - ../(two characters of hex)
     *          - ff/
     *    - addition/ -- folder containing all of the staging file for addition
     *    - removed/ -- folder containing all of the staging file for removed
     *    - branch/ -- folder containing all of the persistent object for branch
     *    - HEAD -- file containing the persistent object for head
     */
    public static void setupPersistence(String msg) {
        // create filesystem (i.e. create directories and folders)
        GITLET_DIR.mkdir();
        COMMITS_FOLDER.mkdir();
        BLOB_FOLDER.mkdir();
        ADDITION_FOLDER.mkdir();
        REMOVED_FOLDER.mkdir();
        BRANCH_FOLDER.mkdir();
        REMOTE_FOLDER.mkdir();

        // create initial commit
        Commit initCommit = makeCommitWithInit(msg);
        String initCommitID= initCommit.getCommitID();

        // create HEAD and master
        saveActiveBranch(masterName, initCommitID);
        saveHEAD(masterName, initCommitID);
    }

    /** remote */
    /** using in push command */
    // fetch all commits and blobs from remote repo by recursion
    private static void push(File remoteDir, Commit commit, String remoteHeadID) {
        String commitID = commit.getCommitID();
        File RemoteCommitsFolder = join(remoteDir, "commits");
        if (commitID.equals(remoteHeadID) || plainFilenamesIn(RemoteCommitsFolder).contains(commitID)) {
            return;
        }
        // save commit
        if (!plainFilenamesIn(RemoteCommitsFolder).contains(commitID)) {
            saveObj(RemoteCommitsFolder, commitID, commit);
        }
        // save blobs with comparing
        for (String blobID : commit.getBlobIDs()) {
            Blob blob = readObject(join(BLOB_FOLDER, getDirID(blobID), blobID), Blob.class);
            saveDirAndObjInBlobs(blob, join(remoteDir, "blobs"), blob.getBlobID());
        }
        // fetch from parent commits(maybe with merge)
        for (String parentID : commit.getParentIDs()) {
            Commit parentCommit = readObject(join(COMMITS_FOLDER, parentID), Commit.class);
            push(remoteDir, parentCommit, remoteHeadID);
        }
    }

    private static boolean isRemoteHeadIDInHistoryOfLocal(String remoteHeadID, Commit commit) {
        // is remote head id in history of local?
        boolean isInHistoryOfLocal = commit.getCommitID().equals(remoteHeadID);
        // check parent commits(maybe with merge)
        for (String parentID : commit.getParentIDs()) {
            Commit parentCommit = readObject(join(COMMITS_FOLDER, parentID), Commit.class);
            return isInHistoryOfLocal || isRemoteHeadIDInHistoryOfLocal(remoteHeadID, parentCommit);
        }
        return isInHistoryOfLocal;
    }

    // save(change) a branch in remote
    public static void saveRemoteBranch(File remoteDir, String branchName, String commitID) {
        Pointer branch = new Pointer(false, branchName, commitID);
        saveObj(join(remoteDir, "branch"), branchName, branch);
    }

    // save(change) HEAD in remote
    // we always store initCommitID in HEAD
    public static void saveRemoteHEAD(File remoteDir, String activeBranchName, String initCommitID) {
        Pointer HEAD = new Pointer(true, activeBranchName, initCommitID);
        saveObj(remoteDir, activeBranchName, HEAD);
    }

    /** using in fetch command */
    // fetch all commits and blobs from remote repo by recursion
    private static void fetch(File remoteDir, Commit commit) {
        String commitID = commit.getCommitID();
        if (commit == null || plainFilenamesIn(COMMITS_FOLDER).contains(commitID)) {
            return;
        }
        // save commit
        if (!plainFilenamesIn(COMMITS_FOLDER).contains(commitID)) {
            saveObj(COMMITS_FOLDER, commitID, commit);
        }
        // save blobs with comparing
        for (String blobID : commit.getBlobIDs()) {
            Blob blob = readObject(join(remoteDir, "blobs", getDirID(blobID), blobID), Blob.class);
            saveDirAndObjInBlobs(blob, BLOB_FOLDER, blob.getBlobID());
        }
        // fetch from parent commits(maybe with merge)
        for (String parentID : commit.getParentIDs()) {
            Commit parentCommit = readObject(join(remoteDir, "commits", parentID), Commit.class);
            fetch(remoteDir, parentCommit);
        }
    }

    private static void validateRemoteDir(Remote remote) {
        // If the remote .gitlet directory does not exist, print the error message and exit
        if (!remote.getRemoteDir().exists()) {
            printErrorWithExit("Remote directory not found.");
        }
    }
}
