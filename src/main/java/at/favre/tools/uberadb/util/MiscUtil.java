package at.favre.tools.uberadb.util;

import java.io.File;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiscUtil {
    public static void zip(File targetZipFile, List<ZipFileDescriptor> filesToZip) throws Exception {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        URI zipUri = new URI("jar:" + targetZipFile.toURI().getScheme(), targetZipFile.toURI().getPath(), null);

        try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, env)) {
            for (ZipFileDescriptor fileDesc : filesToZip) {
                Path externalTxtFile = Paths.get(fileDesc.file.toURI());
                Path pathInZipfile = zipfs.getPath(fileDesc.toZipPath());

                if (fileDesc.subFolder != null && Files.notExists(zipfs.getPath(fileDesc.subFolder))) {
                    Files.createDirectory(zipfs.getPath(fileDesc.subFolder));
                }

                Files.copy(externalTxtFile, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }


    public static class ZipFileDescriptor {
        public final File file;
        public final String subFolder;

        public ZipFileDescriptor(String subFolder, File file) {
            this.file = file;
            this.subFolder = subFolder;
        }

        String toZipPath() {
            if (subFolder != null) {
                return File.separator + subFolder + File.separator + file.getName();
            } else {
                return file.getName();
            }
        }
    }
}
