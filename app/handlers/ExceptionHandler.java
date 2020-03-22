package handlers;

import models.RequestResource;
import play.mvc.Result;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler {

    static ResponseHandler rg;

    public static Result baseErrors(Throwable throwable, RequestResource<Object> finalResource){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String sStackTrace = sw.toString();

        String msg = throwable.getMessage();
        Throwable _throwable = throwable;
        while (null != _throwable) {
            if (_throwable.getClass().toString().contains(".exceptions.jdbc4.")) {
                msg = _throwable.getMessage();
                break;
            }
            if (_throwable.getClass().toString().contains(".jackson.databind.exc.")) {
                msg = _throwable.getMessage();
                break;
            }
            _throwable = _throwable.getCause();
        }

        msg = msg.split("\\r?\\n")[0];
        return rg.generateResponse(finalResource, msg, sStackTrace, "internalServerError");
    }
}
