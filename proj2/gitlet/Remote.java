package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.List;

import static gitlet.MyUtils.*;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Remote implements Serializable {
    private String remoteName;
    private File remoteDir;

    // we don't hard copy of dir, we just use soft link of dir
    public Remote(String remoteName, String dirPathString) {
        this.remoteName = remoteName;
        // get directory
        this.remoteDir = join(dirPathString);
    }

    // get branch names
    public List<String> getBranchNames() {
        return plainFilenamesIn(join(remoteDir, "branch"));
    }

    // get remote name
    public String getRemoteName() {
        return remoteName;
    }

    // get remote directory
    public File getRemoteDir() {
        return remoteDir;
    }
}
