package com.bundle.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

@SuppressWarnings("serial")
public class EncodeDecodeException extends Exception {

    public static String exceptionMessage;

    @Override
    public String getMessage() {
        return EncodeDecodeException.exceptionMessage;
    }

    public String getStackTraceAsString() {
        StringWriter errors = new StringWriter();
        this.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    public static String getStackTraceAsString(EncodeDecodeException e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    public static enum ExceptionCode {
        ARGS_MISMATCHED,
        INVALID_ARGS,
		DECOMPILE_FAILED,
		BUILD_FAILED,
        BUILD_APKS_FAILED,
        SIGNING_FAILED,
        AAPT_FILE_REMOVE_FAILED,
        AAPT_FILE_ADDED_FAILED,
        UNKNOWN_EXCEPTION;
    }

    public EncodeDecodeException(ExceptionCode exceptionCode) {
        switch (exceptionCode) {
            case INVALID_ARGS:
                EncodeDecodeException.exceptionMessage = "'INVALID ARGUMENTS'";
                break;
            case ARGS_MISMATCHED:
                EncodeDecodeException.exceptionMessage = "'ARGUMENTS MISMATCHED'";
                break;

			case BUILD_FAILED:
				EncodeDecodeException.exceptionMessage = "'BUILD_FAILED'";
				break;//BUILD_APKS_FAILED

            case BUILD_APKS_FAILED:
                EncodeDecodeException.exceptionMessage = "'BUILD_APKS_FAILED'";
                break;//BUILD_APKS_FAILED

			case DECOMPILE_FAILED:
				EncodeDecodeException.exceptionMessage = "'DECOMPILE_FAILED'";
				break;

			case SIGNING_FAILED:
				EncodeDecodeException.exceptionMessage = "'SIGNING_FAILED'";
				break;

			case AAPT_FILE_REMOVE_FAILED:
                EncodeDecodeException.exceptionMessage = "'AAPT FILE REMOVE FAILED'\n";
                break;

            case AAPT_FILE_ADDED_FAILED:
                EncodeDecodeException.exceptionMessage = "'AAPT FILE ADDED FAILED'\n";
                break;

            default:
                EncodeDecodeException.exceptionMessage = "Unknown exception has occured\n";
                break;
        }
    }
} 
