package ppj.meteorolog.db.exceptions;

public class RetentionPeriodException extends RuntimeException{

    public RetentionPeriodException(int retentionPeriod, long shardGroupDuration) {
        super("Retention period: " + retentionPeriod+ " cannot be smaller than shard group duration: " + shardGroupDuration);
    }
}
