package ru.pyshinskiy.photon.exception;

public class StorageException extends RuntimeException {

    public StorageException(Exception ex) {
        super(ex);
    }
}
