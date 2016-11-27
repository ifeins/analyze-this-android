/*
 * Copyright (c) 2016 PayPal, Inc.
 *
 * All rights reserved.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package com.example.ifeins.analyze.utils;

import android.support.annotation.NonNull;

import com.google.android.gms.drive.DriveContents;
import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A utility class for Google Drive
 *
 * @author ifeins
 */
public class DriveUtils {

    public static File createTempFileForDriveDocument(File outputFolder, @NonNull DriveContents driveContents) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        File tempFile;
        try {
            tempFile = File.createTempFile("drive_document", ".pdf", outputFolder);
        } catch (IOException e) {
            return null;
        }

        try {
            inputStream = driveContents.getInputStream();
            outputStream = new FileOutputStream(tempFile);
            ByteStreams.copy(inputStream, outputStream);
        } catch (IOException e) {
            tempFile.delete();
            return null;
        } finally {
            if (inputStream != null) {
                try { inputStream.close(); } catch (IOException e) {}
            }
            if (outputStream != null) {
                try { outputStream.close(); } catch (IOException e) {}
            }
        }

        return tempFile;
    }
}
