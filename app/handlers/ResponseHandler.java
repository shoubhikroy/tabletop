package handlers;

import com.fasterxml.jackson.databind.JsonNode;
import models.RequestResource;
import models.ResponseResource;
import play.Logger;
import play.libs.Json;
import play.mvc.Results;
import play.mvc.Result;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static play.mvc.Results.internalServerError;

public class ResponseHandler {
    public static Result generateResponse(RequestResource request, String status, String message, String _resultType) {
        if (request == null)
            request = new RequestResource<>("not_supplied", "error", null);
        if (status == null)
            status = "error";
        if (message == null)
            message = "something went wrong";
        if (_resultType == null)
            _resultType = "badRequest";

        ArrayList<String> errors = new ArrayList<String>();
        errors.add(message);

        return createNoPayloadResponse(request, status, errors, _resultType);
    }

    public static Result generateResponse(RequestResource request, String status, Object payload, String _resultType) {
        return createPayloadResponse(request, status, payload, _resultType);
    }

    public static Result generateResponse(RequestResource request, String status, List<String> errors, String _resultType) {
        if (request == null)
            request = new RequestResource<>("not_supplied", "error", null);
        if (status == null)
            status = "error";
        if (errors == null)
            errors = new ArrayList<String>();
        if (_resultType == null)
            _resultType = "badRequest";

        return createNoPayloadResponse(request, status, errors, _resultType);
    }

    private static Result createNoPayloadResponse(RequestResource request, String status, List<String> er, String _resultType) {
        Method method;
        try {
            ResponseResource rr = new ResponseResource(request.getHash(), request.getEndpoint(), status, er, null);
            JsonNode node = Json.mapper().convertValue(rr, JsonNode.class);
            method = Results.class.getMethod(_resultType, String.class);
            return (Result) method.invoke(null, node.toPrettyString());
        } catch (Exception e) {
            Logger.error("error", e);
            return internalServerError(e.toString());
        }
    }

    private static Result createPayloadResponse(RequestResource request, String status, Object payload, String _resultType) {
        Method method;
        try {
            ResponseResource rr = new ResponseResource(request.getHash(), request.getEndpoint(), status, payload);
            JsonNode node = Json.mapper().convertValue(rr, JsonNode.class);
            method = Results.class.getMethod(_resultType, String.class);
            return (Result) method.invoke(null, node.toPrettyString());
        } catch (Exception e) {
            Logger.error("error", e);
            return internalServerError(e.toString());
        }
    }
}
