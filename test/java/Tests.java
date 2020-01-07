import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import org.junit.*;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

/**
 * Test written using standard "given, when, then." methodology.
 */
@Log4j2
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Tests {
    // https://github.com/cinch-home-services/accel-email-handler/tree/master/docs/index.html
    // https://rawgit.com/nelsw/accel-email-handler/tree/master/docs/index.html
    // https://github.com/<your user name>/<your repo>/blob/master/index.html
    Handler handler = new Handler();

    String goodRequestJson = "{ \"to\": \"cvanelswyk@cchs.com\", \"from\": \"connectedhometeam@cchs.com\", \"subject\": \"Password recovery help has arrived!\", \"body\": \"888-000\", \"bucket\": \"nelsw\", \"key\": \"email-confirmation.html\" }",
            badRequestJson = "{ \"to\": \"cvanelswyk@cchs.com\", \"from\": \"connectedhometeam@cchs.com\", \"subject\": \"Email confirmation code has arrived!\", \"body\": \"ABWFS7QDRAE3XVJFZDOVACC5VDS3JBFFMVWWC2LMXFRW63TON5ZHMYLOMVWHG53ZNNAGO3LBNFWC4Y3PNWSWM33SMNS4FM3UO5XV6ZTBMN2G64S7OZSXE2LGNFSWJQV2MZXXEY3FMRPXOZLBNNPXAYLTON3W64TEL5ZGK43FOTBA\", \"bucket\": \"n\", \"key\": \"password-reset.html\" }",
            plainEmailJson = "{ \"to\": \"cvanelswyk@cchs.com\", \"from\": \"connectedhometeam@cchs.com\", \"subject\": \"TEST SUBJECT\", \"body\": \"<h1>HELLO WORLD!</h1>\" }";

    @NonFinal APIGatewayProxyRequestEvent request;
    @NonFinal APIGatewayProxyResponseEvent response;

    /**
     * Method executed prior to any @Test method.
     */
    @Before
    public void before() {
        request = new APIGatewayProxyRequestEvent();
        request.setBody(goodRequestJson);
    }

    /**
     * Tests the golden path.
     */
    @Test
    public void validRequest_handleRequest_returnSuccess() {
        response = handler.handleRequest(request,null);
        assertEquals(200, (int) response.getStatusCode());
    }

    /**
     * Tests empty email html.
     */
    @Test
    public void badRequest_handleRequest_returnClientError() {
        request.setBody(badRequestJson);
        response = handler.handleRequest(request,null);
        assertEquals(400, (int) response.getStatusCode());
    }

    /**
     * Tests a near impossible case but we need 100% code coverage.
     */
    @Test
    public void nullRequest_handleRequest_returnServerError() {
        response = handler.handleRequest(null,null);
        assertEquals(500, (int) response.getStatusCode());
    }

}
