import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.StringUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler responsible for sending Cinch Accelerate emails.
 * See {@link Client} and {@link Email} for further details.
 * @author connorvanelswyk
 */
@Log4j2
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    /**
     * {@link APIGatewayProxyResponseEvent} headers, {@link #response}
     */
    static Map<String, String> headers;

    /**
     * Used to find and replace a single variable in email templates.
     */
    static String code;

    /*
     * Static initialization and construction for static variables.
     */
    static {
        code = "{{.Code}}";
        headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
    }

    /**
     * Primary entry point for this λƒ.
     *
     * @param r the {@link APIGatewayProxyRequestEvent}, see mock-request.json.
     * @param c the {@link Context} in which this ƒ is executed.
     * @return {@link APIGatewayProxyResponseEvent}
     */
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent r, Context c) {
        try {
            log.info("request=[{}]", r);
            Email email = Client.getInstance().unmarshalEmail(r.getBody());
            String html = email.getBody();
            if (!StringUtils.isNullOrEmpty(email.getBucket()) && !StringUtils.isNullOrEmpty(email.getKey())) {
                html = Client.getInstance().getHtml(email.getBucket(), email.getKey());
                if (!StringUtils.isNullOrEmpty(email.getBody())) {
                    html = StringUtils.replace(html, code, email.getBody());
                }
            }
            if (StringUtils.isNullOrEmpty(html)) {
                return response(HttpStatus.SC_BAD_REQUEST, "empty email body");
            } else {
                Client.getInstance().sendEmail(email.getFrom(), email.getTo(), email.getSubject(), html);
                return response(HttpStatus.SC_OK, "success");
            }
        } catch (Exception e) {
            log.error(e);
            return response(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Helper method for returning API Gateway Proxy Response Events.
     *
     * @param statusCode {@code Integer} server code returned to the client
     * @param body {@code String} response body returned to the client
     * @return a sufficiently populated {@link APIGatewayProxyResponseEvent}
     */
    private static APIGatewayProxyResponseEvent response(int statusCode, String body) {
        APIGatewayProxyResponseEvent r = new APIGatewayProxyResponseEvent();
        r.setIsBase64Encoded(false);
        r.setHeaders(headers);
        r.setStatusCode(statusCode);
        r.setBody(body);
        log.debug("response=[{}]", r);
        return r;
    }

}
