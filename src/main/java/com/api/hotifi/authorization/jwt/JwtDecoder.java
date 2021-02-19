package com.api.hotifi.authorization.jwt;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Date;
/*
    -------------------- JWT Decoder Json Example ------------------
    {
        alg: "HS256",
        typ: "JWT"
    }.
    {
         exp: 1613661320,
         user_name: "sahoosagyan20@gmail.com",
         authorities: [
          "CUSTOMER"
         ],
         jti: "iwz82Ii1GdFvuhlZJGqCpR1BmIQ",
         client_id: "client",
         scope: [
          "read",
          "write"
         ]
    }.
    [signature]
*/

@Service
public class JwtDecoder {

    public String extractUsername(String token) {
        JSONObject jsonObject = extractJwtBody(token);
        return jsonObject.getString("user_name");
    }

    public Date extractExpiryDate(String token) {
        JSONObject jsonObject = extractJwtBody(token);
        long epoch = jsonObject.getLong("exp");
        System.out.println("jsontime : " + epoch);
        return new Date(epoch * 1000); //epoch is number of seconds since Jan 1, 1970
    }

    public JSONObject extractJwtHeader(String token){
        String[] split_string = token.split("\\.");
        String base64EncodedHeader = split_string[0];
        Base64 base64Url = new Base64(true);
        String header = new String(base64Url.decode(base64EncodedHeader));
        return new JSONObject(header);
    }

    public JSONObject extractJwtBody(String token){
        String[] split_string = token.split("\\.");
        String base64EncodedBody = split_string[1];
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(base64EncodedBody));
        return new JSONObject(body);
    }

    public JSONObject extractJwtSignature(String token){
        String[] split_string = token.split("\\.");
        String base64EncodedSignature = split_string[2];
        Base64 base64Url = new Base64(true);
        String signature = new String(base64Url.decode(base64EncodedSignature));
        return new JSONObject(signature);
    }

    public boolean isTokenExpired(String token)
    {
        Date currentTime = new Date(System.currentTimeMillis());
        Date expiryTime = extractExpiryDate(token);
        return currentTime.after(expiryTime);
    }

    public boolean validateToken(String token, String username) {
        String jwtUsername = extractUsername(token);
        return username.equals(jwtUsername) && !isTokenExpired(token);
    }
}
