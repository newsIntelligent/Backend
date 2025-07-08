package UMC.news.newsIntelligent.global.apiPayload.exception;

import UMC.news.newsIntelligent.global.apiPayload.code.error.BaseErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final BaseErrorCode code;

    public CustomException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode;
    }
}
