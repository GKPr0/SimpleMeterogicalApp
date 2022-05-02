package ppj.meteorolog.db.exceptions;

public class BucketNotFoundException extends RuntimeException {
    public BucketNotFoundException(String bucket) {
        super("Bucket " + bucket + " not found");
    }
}

