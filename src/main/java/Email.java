import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * Class representing the body of an API Gateway Proxy Request, received by the {@link Handler}.
 * @author connorvanelswyk
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Email {

    /**
     * Sender email address.
     */
    String from;

    /**
     * Recipient email address.
     */
    String to;

    /**
     * Email subject.
     */
    String subject;

    /**
     * Email body, plaint text or {@code HTML}.
     */
    String body;

    /**
     * AWS S3 directory for potential email template.
     */
    String bucket;

    /**
     * AWS S3 filename for potential email template.
     */
    String key;

}
