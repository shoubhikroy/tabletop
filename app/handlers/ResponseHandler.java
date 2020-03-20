package handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ErrorResource;
import models.RequestResource;
import models.ResponseResource;
import play.Logger;
import play.mvc.Results;
import play.mvc.Result;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static play.mvc.Results.internalServerError;

public class ResponseHandler {
    public static Result generatedResponse(RequestResource request, String status, String message, String _resultType) {
        if (request == null)
            request = new RequestResource<>("not_supplied", "error", null);
        if (status == null)
            status = "error";
        if (message == null)
            message = "something went wrong";
        if (_resultType == null)
            _resultType = "badRequest";

        Method method;
        ArrayList<String> errors = new ArrayList<String>();
        errors.add(message);
        ErrorResource er = new ErrorResource(errors);
        try {
            ResponseResource rr = new ResponseResource(request.getHash(), request.getEndpoint(), status, er, null);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.convertValue(rr, JsonNode.class);
            method = Results.class.getMethod(_resultType, String.class);
            return (Result) method.invoke(null, node.toPrettyString());
        } catch (Exception e) {
            Logger.error("error", e);
            return internalServerError(e.toString());
        }
    }

    public Result generatedResponse(RequestResource request, String status, Object payload, String _resultType) {

        return null;
    }

    public Result generatedResponse(RequestResource request, String status, List<String> errors, String _resultType) {

        return null;
    }
}
