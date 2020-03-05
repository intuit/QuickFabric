package com.intuit.quickfabric.commons.utils;

import com.intuit.quickfabric.commons.exceptions.QuickFabricBaseException;
import com.intuit.quickfabric.commons.vo.ApiErrorVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;

public class CommonUtils {
    private static final Logger logger = LogManager.getLogger(CommonUtils.class);

    public static Optional<Integer> tryParseInt(String intValue) {
        try {
            return Optional.ofNullable(Integer.parseInt(intValue));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * @param error logs errorResponse
     */
    public static void logErrorResponse(Exception error) {
        createErrorResponse(error);
    }

    public static ResponseEntity createErrorResponse(Exception error) {
        ApiErrorVO errorVO;
        HttpStatus status;
        if (error instanceof QuickFabricBaseException) {
            QuickFabricBaseException baseException = (QuickFabricBaseException) error;
            Throwable innerException = baseException.getCause();
            status = baseException.getHttpStatusCode();
            if (innerException != null) {
                errorVO = new ApiErrorVO(baseException.getMessage(), getInnerExceptionMessage(innerException));
            } else {
                errorVO = new ApiErrorVO(baseException.getMessage());
            }
        } else if (error instanceof AuthenticationException) {
            status = HttpStatus.UNAUTHORIZED;
            errorVO = new ApiErrorVO(error.getMessage());
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            String message = StringUtils.isBlank(error.getMessage()) ? error.toString() : error.getMessage();
            if (error.getCause() != null) {
                errorVO = new ApiErrorVO(message, error.getCause().getMessage());
            } else {
                errorVO = new ApiErrorVO(message);
            }
        }

        logger.error("API error.", error);
        return ResponseEntity.status(status).body(errorVO);
    }

    private static String getInnerExceptionMessage(Throwable innerException) {
        String innerMessage = innerException.getMessage();
        if (innerException instanceof RestClientResponseException) {
            RestClientResponseException restClientResponseException = (RestClientResponseException) innerException;
            if (StringUtils.isNotBlank(restClientResponseException.getResponseBodyAsString())) {
                innerMessage = restClientResponseException.getResponseBodyAsString();
                try {
                    JSONObject responseJson = new JSONObject(restClientResponseException.getResponseBodyAsString());
                    if (responseJson.has("message") && StringUtils.isNotBlank(responseJson.getString("message"))) {
                        innerMessage = responseJson.getString("message");
                    }
                } catch (JSONException e) {
                    logger.error("Error happened while parsing innner exception response: " + restClientResponseException.getResponseBodyAsString());
                }
            }
        }

        return innerMessage;
    }
}