
package de.lystx.cloudsystem.library.service.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip {

    /**
     * Adds a file to a zpfile
     * @param base
     * @param filePath
     * @param outZipStream
     * @throws IOException
     */
    private void addFileToZip(File base, String filePath, ZipOutputStream outZipStream) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(new File(base, filePath)); ) {
            int BUFFER_SIZE_BYTES = 1024;
            byte[] buffer = new byte[BUFFER_SIZE_BYTES];
            outZipStream.putNextEntry(new ZipEntry(filePath));
            int bytes_read;
            while ((bytes_read = inputStream.read(buffer)) > 0) {
                outZipStream.write(buffer, 0, bytes_read);
            }
        } finally {
            outZipStream.closeEntry();
        }
    }

    /**
     * Adds a directory to a zipFile
     * @param base
     * @param directory
     * @param outZipStream
     * @throws IOException
     */
    private void addDirectoryToZip(File base, String directory, ZipOutputStream outZipStream) throws IOException {
        File fullPath = new File(base, directory).getAbsoluteFile();
        URI baseURI = base.toURI();

        String[] fileNames = fullPath.list();
        for (String fileName : fileNames) {
            File file = new File(fullPath, fileName);
            if (!file.exists()) {
                continue;
            }
            String relativePath = baseURI.relativize(file.toURI()).getPath();
            if (file.isFile()) {
                addFileToZip(base, relativePath, outZipStream);
            } else if (file.isDirectory()) {
                addDirectoryToZip(base, relativePath, outZipStream);
            }
        }
    }

    /**
     * Main method to zip a directory
     * @param directory
     * @param outFile
     * @throws IOException
     */
    public void zip(File directory, File outFile) {
        try {
            int BUFFER_OUTPUT_STREAM = 256 * 1024;
            try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outFile), BUFFER_OUTPUT_STREAM);
                 ZipOutputStream outZipStream = new ZipOutputStream(outputStream); ) {
                addDirectoryToZip(directory, ".", outZipStream);
                outZipStream.flush();
                outZipStream.finish();
            }
        } catch (IOException ignored) {

        }
    }
}