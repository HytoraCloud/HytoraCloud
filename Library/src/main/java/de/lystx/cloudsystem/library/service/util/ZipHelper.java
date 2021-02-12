package de.lystx.cloudsystem.library.service.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
/**
 * Coded By CryCodes
 * Class: ZipHelper
 * Date : 16.08.2020
 * Time : 20:38
 * Project: LiptonCloud 2.0
 */



public class ZipHelper {

    private final File src;
    private final File dest;

    private final ArrayList<File> files;

    /**
     * Constructor
     *
     * @param src
     *            The File/Directory to be zipped
     * @param dest
     *            The Destination of the zipped file/directory
     */
    public ZipHelper(File src, File dest) {
        this.src = src;
        this.dest = dest;
        this.files = new ArrayList<>();
    }

    /**
     * Starts the Zip process
     *
     * @return The Location of the zipped file
     */
    public File zip() {
        this.getAllFiles(this.src);

        try {
            FileOutputStream fos = new FileOutputStream(this.dest);
            ZipOutputStream zos = new ZipOutputStream(fos);

            for (File file : this.files) {
                if (file.isDirectory()) {
                    continue;
                }
                FileInputStream fis = new FileInputStream(file);
                String zipPath = file.getCanonicalPath().substring(this.src.getCanonicalPath().length() + 1);
                ZipEntry entry = new ZipEntry(zipPath);
                zos.putNextEntry(entry);

                byte[] bytes = new byte[1024];
                int ln;
                while ((ln = fis.read(bytes)) >= 0) {
                    zos.write(bytes, 0, ln);
                }

                zos.closeEntry();
                fis.close();
            }

            zos.close();
            fos.close();
            return this.dest;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Unzips a File
     *
     * @return The Directory the file got extracted to
     */
    public File unzip() {
        byte[] buffer = new byte[1024];
        try {
            if (!this.dest.exists()) {
                this.dest.mkdir();
            }

            ZipInputStream zis = new ZipInputStream(new FileInputStream(this.src));
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(this.dest, fileName);
                new File(newFile.getParent()).mkdirs();

                // deepcode ignore PT: F
                FileOutputStream fos = new FileOutputStream(newFile);

                int ln;
                while ((ln = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, ln);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            return this.dest;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds all Files/Directories of a Directory to the File list
     *
     * @param dir The Directory
     */
    private void getAllFiles(File dir) {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            this.files.add(file);
            if (file.isDirectory()) {
                this.getAllFiles(file);
            }
        }
    }

}