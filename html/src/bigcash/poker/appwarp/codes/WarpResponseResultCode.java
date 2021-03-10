package bigcash.poker.appwarp.codes;

public interface WarpResponseResultCode {
    int SUCCESS=0;
    int AUTH_ERROR=1;
    int RESOURCE_NOT_FOUND=2;
    int RESOURCE_MOVED=3;
    int BAD_REQUEST=4;
    int CONNECTION_ERROR=5;
    int UNKNOWN_ERROR=6;
    int SIZE_ERROR=7;
    int SUCCESS_RECOVERED=8;
    int CONNECTION_ERROR_RECOVERABLE=9;
}
