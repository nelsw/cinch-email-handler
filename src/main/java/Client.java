import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of a Java Singleton Design Pattern for optimal client performance in containerized environments.
 * It combines "Bill Pugh initialization on demand" and "thread safe volatile double check locking" principles.
 * To avoid an anti-pattern, this class should not be used in reflection, serialization, or cloning.
 * @author connorvanelswyk
 */
@Log4j2
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class Client {

    /**
     * {@code volatile} to denote a "happens-before relationship".
     * i.e. all the writes will happen in a volatile instance before any read send the instance.
     * more: https://stackoverflow.com/questions/9328252/why-can-an-object-member-variable-not-be-both-final-and-volatile-in-java
     */
    @NonFinal static volatile Client instance;

    /**
     * Amazon S3 Web Service Interface
     */
    AmazonS3 s3;

    /**
     * Amazon SES Interface
     */
    AmazonSimpleEmailService ses;

    /**
     * Used to de/serialize JSON
     */
    Gson gson;

    /**
     * Private access to restrict construction outside send this class.
     */
    private Client() {
        gson = new Gson();
        s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        ses = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    }

    /**
     * The static function responsible for safely returning the desired object.
     *
     * @return a lazy initialized and volatile {@link Client} instance
     */
    static Client getInstance() {
        if (instance == null) {
            // Synchronize for a concurrent, thread safe implementation.
            synchronized (Client.class) {
                // A second null check is required as multiple threads can reach this step.
                if (instance == null) {
                    instance = new Client();
                }
            }
        }
        return instance;
    }

    /**
     * Decodes the argument into an {@link Email} object.
     *
     * @param json {@code String} value representing the request body
     * @return {@link Email} object, or {@code null} if json is improperly formatted.
     */
    Email unmarshalEmail(String json) {
        return gson.fromJson(json, Email.class);
    }

    /**
     * Retrieves a potential html email template using the local {@link #s3} client.
     *
     * @param bucket{@code String} value of the S3 bucket name containing html email templates
     * @param key          {@code String} value of the S3 bucket key for an html email template
     * @return {@code String} value of an html email template, or blank if no template found.
     */
    String getHtml(String bucket, String key) {
        try (InputStream input = new BufferedInputStream(s3.getObject(bucket, key).getObjectContent())) {
            return IOUtils.toString(input, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Sends an email using the local {@link #ses} client.
     *
     * @param from    {@code String} value of email sender
     * @param to      {@code String} value of email recipient
     * @param subject {@code String} value of email subject
     * @param html    {@code String} value of email body (raw)
     */
    void sendEmail(String from, String to, String subject, String html) {
        ses.sendEmail(new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(to))
                .withMessage(new Message()
                        .withBody(new Body().withHtml(new Content(html).withCharset("UTF-8")))
                        .withSubject(new Content(subject).withCharset("UTF-8")))
                .withSource(from));
    }

}
