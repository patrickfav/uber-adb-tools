package at.favre.tools.uberadb.util;

import java.io.File;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiscUtil {
    public static void zip(File targetZipFile, List<File> filesToZip) throws Exception {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        URI zipUri = new URI("jar:" + targetZipFile.toURI().getScheme(), targetZipFile.toURI().getPath(), null);

        try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, env)) {
            for (File file : filesToZip) {
                Path externalTxtFile = Paths.get(file.toURI());
                Path pathInZipfile = zipfs.getPath(file.getName());
                Files.copy(externalTxtFile, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

}
