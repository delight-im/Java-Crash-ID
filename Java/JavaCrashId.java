package im.delight.java.crashid;

/*
 * Copyright (c) delight.im <info@delight.im>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

/** Generates unique fingerprints for crashes on Android and the JVM */
public class JavaCrashId {

    private static final String[] UNPERTINENT_PACKAGES = new String[] {
        "java.",
        "javax.",
        "android.",
        "com.android.internal.",
        "dalvik.system."
    };
    private static final char SEPARATOR = '/';
    private static final char METHOD_PREFIX = '#';
    private static final char LINE_NUMBER_PREFIX = '@';
    private static final char CAUSE_PREFIX = '<';
    private static final String HASH_ALGORITHM = "SHA-1";
    private static final String CHARSET = "UTF-8";
    private static final String HEX_FORMAT = "%02x";

    /**
     * Returns the unique fingerprint for the given `Throwable` instance
     *
     * @param throwable the instance of `Throwable` or its subclasses
     * @return the fingerprint
     */
    public static String from(final Throwable throwable) {
    	return from(throwable, false);
    }

    /**
     * Returns the unique fingerprint for the given `Throwable` instance
     *
     * @param throwable the instance of `Throwable` or its subclasses
     * @param raw whether to return the raw fingerprint (`true`) or its hash (`false`)
     * @return the fingerprint
     */
    public static String from(final Throwable throwable, final boolean raw) {
    	final StringBuilder output = new StringBuilder();
    	createThrowableId(throwable, output);
        final String fingerprint = output.toString();

        if (raw) {
        	return fingerprint;
        }
        else {
        	try {
    			return hash(fingerprint);
    		}
        	catch (NoSuchAlgorithmException e) {
        		throw new RuntimeException(e);
    		}
        	catch (UnsupportedEncodingException e) {
        		throw new RuntimeException(e);
    		}
        }
    }

    private static void createThrowableId(final Throwable throwable, final StringBuilder output) {
    	output.append(throwable.toString());

        for (StackTraceElement stackFrame : throwable.getStackTrace()) {
        	if (!isPertinentClass(stackFrame.getClassName())) {
        		continue;
        	}

        	output.append(SEPARATOR);
            output.append(stackFrame.getClassName());
            output.append(METHOD_PREFIX);
            output.append(stackFrame.getMethodName());
            output.append(LINE_NUMBER_PREFIX);
            output.append(stackFrame.getLineNumber());
        }

        final Throwable cause = throwable.getCause();
        if (cause != null) {
        	output.append(CAUSE_PREFIX);
        	createThrowableId(cause, output);
        }
    }

    private static boolean isPertinentClass(final String className) {
        for (String ignoredPackage : UNPERTINENT_PACKAGES) {
            if (className.startsWith(ignoredPackage)) {
                return false;
            }
        }

        return true;
    }

    private static String hash(final String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    	return hash(input.getBytes(CHARSET));
    }

    private static String hash(final byte[] input) throws NoSuchAlgorithmException {
    	final MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
    	return bytesToHex(md.digest(input));
    }

    private static String bytesToHex(final byte[] bytes) {
    	final StringBuilder sb = new StringBuilder();

    	for (byte b : bytes) {
    		sb.append(String.format(HEX_FORMAT, b));
    	}

    	return sb.toString();
    }

}
