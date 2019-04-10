package utils;

import models.BufferReadResponse;

import java.io.*;

public class FileManager {
    private static FileManager ourInstance = new FileManager();
    private FileInputStream fileInputStream = null;
    private boolean isFileEnded = false;

    public static FileManager getInstance() {
        return ourInstance;
    }

    private FileManager() {
    }

    public void setupFile(File file) {
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public BufferReadResponse readNextBuffer() {
        byte[] readBuffer = new byte[1024];
        int readNoOfBytes=0;
        if (!isFileEnded) {
            try {
                readNoOfBytes = fileInputStream.read(readBuffer);
                if (readNoOfBytes != readBuffer.length) {
                    isFileEnded = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new BufferReadResponse(readBuffer,readNoOfBytes);
    }

}
