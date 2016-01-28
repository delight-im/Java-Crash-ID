<?php

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

/** Generates unique fingerprints for crashes on Android and the JVM */
class JavaCrashId {

	const STACK_TRACE_ELEMENTS_REGEX = '/^\\s*(.*?)\\s*$/mi';
	const STACK_TRACE_LINE_REGEX = '/at ([a-zA-Z0-9$_.]+?)\\.([a-zA-Z0-9_]+)\\([a-zA-Z0-9_.]+\\:([0-9]+)\\)/i';
	const CAUSED_ORIGINAL_PREFIX = 'Caused by: ';
	const SEPARATOR = '/';
	const METHOD_PREFIX = '#';
	const LINE_NUMBER_PREFIX = '@';
	const CAUSE_PREFIX = '<';
	const HASH_ALGORITHM = 'sha1';

    private static $unpertinentPackages = array(
        'java.',
        'javax.',
        'android.',
        'com.android.internal.',
        'dalvik.system.'
    );

    /**
     * Returns the unique fingerprint for the given stack trace
     *
     * @param $throwable the stack trace as a string
     * @param $raw whether to return the raw fingerprint (`true`) or its hash (`false`)
     * @return the fingerprint
     */
    public static function from($throwable, $raw = false) {
    	$output = array();
    	self::createThrowableId($throwable, $output);
        $fingerprint = implode('', $output);

        if ($raw) {
        	return $fingerprint;
        }
        else {
			return self::hash($fingerprint);
        }
    }

	private static function createThrowableId($throwable, &$output) {
		if (preg_match_all(self::STACK_TRACE_ELEMENTS_REGEX, $throwable, $matches)) {
			$output[] = $matches[1][0];

			$numStackTraceElements = count($matches[1]);
			for ($i = 1; $i < $numStackTraceElements; $i++) {
				if (self::startsWith($matches[1][$i], self::CAUSED_ORIGINAL_PREFIX)) {
					$output[] = self::CAUSE_PREFIX;
					$output[] = substr($matches[1][$i], strlen(self::CAUSED_ORIGINAL_PREFIX));

					continue;
				}

				if (preg_match(self::STACK_TRACE_LINE_REGEX, $matches[1][$i], $line)) {
					if (!self::isPertinentClass($line[1])) {
						continue;
					}

					$output[] = self::SEPARATOR;
					$output[] = $line[1];
					$output[] = self::METHOD_PREFIX;
					$output[] = $line[2];
					$output[] = self::LINE_NUMBER_PREFIX;
					$output[] = $line[3];
				}
			}

			return $output;
		}
		else {
			return '';
		}
	}

    private static function isPertinentClass($className) {
        foreach (self::$unpertinentPackages as $ignoredPackage) {
            if (self::startsWith($className, $ignoredPackage)) {
                return false;
            }
        }

        return true;
    }

	private static function hash($input) {
		return hash(self::HASH_ALGORITHM, $input);
	}

	private static function startsWith($haystack, $needle) {
		$length = strlen($needle);
		return (substr($haystack, 0, $length) === $needle);
	}

}
