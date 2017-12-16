/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eb.ocr.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author de1usrs0
 */
public class FileListFilter implements FilenameFilter {

    private final String name;
    private final String extension;

    public FileListFilter(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }

    @Override
    public boolean accept(File directory, String filename) {
        boolean fileOK = true;

        if (name != null)
            fileOK &= filename.startsWith(name);

        if (extension != null)
            fileOK &= filename.endsWith('.' + extension);
        return fileOK;
    }
}

