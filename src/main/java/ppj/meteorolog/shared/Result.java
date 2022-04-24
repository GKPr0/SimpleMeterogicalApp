package ppj.meteorolog.shared;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result<T>{
    private Boolean isSuccess;
    private T value;
    private String error;

    public static <I> Result<I> success(I value){
        Result<I> result = new Result<>();
        result.setIsSuccess(true);
        result.setValue(value);
        return result;
    }

    public static <I> Result<I> failure(String error){
        Result<I> result = new Result<>();
        result.setIsSuccess(false);
        result.setError(error);
        return result;
    }
}
