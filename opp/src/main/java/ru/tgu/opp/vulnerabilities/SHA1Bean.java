package ru.tgu.opp.vulnerabilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1Bean {

    public MessageDigest getSHA1() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256");
    }
}
