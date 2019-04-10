package utils;

import java.io.File;

import models.BufferReadResponse;

public class BufferManager {
    private static BufferManager ourInstance = new BufferManager();
    private byte[] buffer1;
    private byte[] buffer2;
    private int lexemeForward = 0;
    private int bufferIndex = 1;
    private boolean isEOF = false;
    private int currentLineNumber = 1;
    private int columnNumber = 0;

    public static BufferManager getInstance() {
        return ourInstance;
    }

    private BufferManager() {
    }

    public boolean isEOF() {
        return isEOF;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public int getCurrentLineNumber() {
        return currentLineNumber;
    }

    public void initialize(File file) {
        FileManager.getInstance().setupFile(file);
        BufferReadResponse bufferReadResponse = FileManager.getInstance().readNextBuffer();
        if (bufferReadResponse.getNoOfBytesRead() > 0) {
            buffer1 = bufferReadResponse.getBuffer();
        }
        bufferReadResponse = FileManager.getInstance().readNextBuffer();
        if (bufferReadResponse.getNoOfBytesRead() > 0) {
            buffer2 = bufferReadResponse.getBuffer();
        }

    }

    public char getNextCharFromBuffer() {
        char charfromBuffer = 0;
        switch (bufferIndex) {
            case 1:
                if (buffer1 != null) {
                    charfromBuffer = (char) buffer1[lexemeForward++];
                    columnNumber++;
                    if (lexemeForward >= buffer1.length) {
                        lexemeForward = 0;
                        bufferIndex = 2;
                        buffer1 = null;
                        BufferReadResponse bufferReadResponse = FileManager.getInstance().readNextBuffer();
                        if (bufferReadResponse.getNoOfBytesRead() > 0) {
                            buffer1 = bufferReadResponse.getBuffer();
                        }
                    }
                } else {
                    isEOF = true;
                }

                break;
            case 2:
                if (buffer2 != null) {
                    charfromBuffer = (char) buffer2[lexemeForward++];
                    columnNumber++;
                    if (lexemeForward >= buffer2.length) {
                        lexemeForward = 0;
                        bufferIndex = 1;
                        buffer2 = null;
                        BufferReadResponse bufferReadResponse = FileManager.getInstance().readNextBuffer();
                        if (bufferReadResponse.getNoOfBytesRead() > 0) {
                            buffer2 = bufferReadResponse.getBuffer();
                        }
                    }
                } else {
                    isEOF = true;
                }

                break;
        }
        if(charfromBuffer=='\n'){
            currentLineNumber++;
            columnNumber = 0;
        }
        return charfromBuffer;
    }

    public void skipThisLine() {
        char nextChar = getNextCharFromBuffer();
        while(nextChar!='\n') {
             nextChar = getNextCharFromBuffer();
        }
    }
}
