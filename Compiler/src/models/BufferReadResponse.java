package models;

public class BufferReadResponse {

   private byte[] buffer;
   private int noOfBytesRead;

    public BufferReadResponse(byte[] buffer, int noOfBytesRead) {
        this.buffer = buffer;
        this.noOfBytesRead = noOfBytesRead;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public int getNoOfBytesRead() {
        return noOfBytesRead;
    }
}
