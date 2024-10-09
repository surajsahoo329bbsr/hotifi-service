package com.api.hotifi.payment.processor.razorpay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.SignatureException;

/**
 * This class defines common routines for generating
 * authentication signatures for Razorpay Webhook requests.
 */
public class RazorpayVerificationUtils {

    @Value("${razorpay.secret}")
    private static String razorpayClientSecret;

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    private static final Logger logger = LoggerFactory.getLogger(RazorpayVerificationUtils.class);

    /**
     * Computes RFC 2104-compliant HMAC signature.
     * * @param data
     * The data to be signed.
     *
     * @param data The signing key.
     * @return The Base64-encoded RFC 2104-compliant HMAC signature.
     * @throws java.security.SignatureException when signature generation fails
     */
    public static String calculateRFC2104HMAC(String data, String secret)
            throws java.security.SignatureException {
        String result;
        try {

            // get an hmac_sha256 key from the raw secret bytes
            SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA256_ALGORITHM);

            // get an hmac_sha256 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes());

            // base64-encode the hmac
            result = DatatypeConverter.printHexBinary(rawHmac).toLowerCase();

        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }

    public static boolean verifyRazorpaySignature(String orderId, String razorpayPaymentId, String razorpaySignature) {
        try {
            String generatedSignature =
                    calculateRFC2104HMAC(orderId + "|" + razorpayPaymentId, razorpayClientSecret);
            return generatedSignature.equals(razorpaySignature);
        } catch (SignatureException e) {
            logger.error("An error occurred : {}", e.getMessage(), e);
        }
        return false;
    }
}
