package org.netarchivesuite.heritrix3wrapper.unzip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

public class UnzipUtils {

    /** Size of the buffer to read/write data. */
    private static final int BUFFER_SIZE = 8192;

    private byte[] tmpBuffer = new byte[BUFFER_SIZE];

    public void unzip(String zipFilePath, String dstDirStr) throws IOException {
        File dstDir = new File(dstDirStr);
        if (!dstDir.exists()) {
            dstDir.mkdir();
        }
        List<FileModeDate> fileModeDateBacklog = new LinkedList<FileModeDate>();
        FileModeDate fileModeDate;
        ZipFile zipFile = new ZipFile(zipFilePath);
        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        ZipArchiveEntry entry;
        String name;
        InputStream in;
        while (entries.hasMoreElements()) {
            entry = entries.nextElement();
            name = entry.getName();
            File dstFile = new File(dstDir, name);
            long lastModified = entry.getTime();
            if (entry.isDirectory()) {
                dstFile.mkdir();
                fileModeDate = new FileModeDate();
                fileModeDate.name = name;
                fileModeDate.file = dstFile;
                if (entry.getPlatform() == ZipArchiveEntry.PLATFORM_UNIX) {
                    fileModeDate.perms = unixModeToPosixSet(entry.getUnixMode());
                }
                if (lastModified != -1) {
                    fileModeDate.lastModified = lastModified;
                }
                fileModeDateBacklog.add(fileModeDate);
            } else if (entry.isUnixSymlink()) {
            } else {
                in = zipFile.getInputStream(entry);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dstFile));
                int read = 0;
                while ((read = in.read(tmpBuffer)) != -1) {
                    bos.write(tmpBuffer, 0, read);
                }
                bos.close();
                in.close();
                if (entry.getPlatform() == ZipArchiveEntry.PLATFORM_UNIX) {
                    int unixMode = entry.getUnixMode();
                    Set<PosixFilePermission> perms = unixModeToPosixSet(unixMode);
                    Files.setPosixFilePermissions(dstFile.toPath(), perms);
                    //System.out.println(String.format("%o", unixMode) + " " + entry.getName());
                }
                if (lastModified != -1) {
                    dstFile.setLastModified(entry.getTime());
                }
            }
        }
        zipFile.close();
        Collections.sort(fileModeDateBacklog, new Comparator<FileModeDate>() {
            @Override
            public int compare(FileModeDate o1, FileModeDate o2) {
                return o2.name.length() - o1.name.length();
            }});
        Iterator<FileModeDate> iter = fileModeDateBacklog.iterator();
        while (iter.hasNext()) {
            fileModeDate = iter.next();
            if (fileModeDate.perms != null) {
                Files.setPosixFilePermissions(fileModeDate.file.toPath(), fileModeDate.perms);
            }
            if (fileModeDate.lastModified != -1) {
                fileModeDate.file.setLastModified(fileModeDate.lastModified);
            }
        }
    }

    public class FileModeDate {
        public String name;
        public File file;
        public Set<PosixFilePermission> perms = null;
        public long lastModified = -1;
    }

    public static PosixFilePermission[] posixFilePermissionsBitSorted = {
        PosixFilePermission.OTHERS_EXECUTE,
        PosixFilePermission.OTHERS_WRITE,
        PosixFilePermission.OTHERS_READ,
        PosixFilePermission.GROUP_EXECUTE,
        PosixFilePermission.GROUP_WRITE,
        PosixFilePermission.GROUP_READ,
        PosixFilePermission.OWNER_EXECUTE,
        PosixFilePermission.OWNER_WRITE,
        PosixFilePermission.OWNER_READ
    };

    public Set<PosixFilePermission> unixModeToPosixSet(int unixMode) {
        Set<PosixFilePermission> permissions = new TreeSet<PosixFilePermission>();
        int bit = 8;
        //StringBuilder sb = new StringBuilder();
        while (bit >= 0) {
            if ((unixMode & (1 << bit)) != 0) {
                permissions.add(posixFilePermissionsBitSorted[bit]);
                //sb.append("1");
            } else {
                //sb.append("0");
            }
            --bit;
        }
        //System.out.println(sb.toString());
        return permissions;
    }

}
