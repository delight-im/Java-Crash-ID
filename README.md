# Java Crash ID

Generates unique fingerprints for crashes on Android and the JVM

## Usage

### Java

`JavaCrashId.from(exception)`

### PHP

`JavaCrashId::from($stackTraceString)`

## Example

### Output of `JavaCrashId.from(exception)`

```
b652f373781c9920862bcefa01197b6777bd8e4e
```

### Output of `JavaCrashId.from(exception, true)`

```
java.lang.RuntimeException: An error occured while executing doInBackground()<java.lang.NullPointerException/com.my.package.MainActivity$MyAsyncTask#doInBackground@254/com.my.package.MainActivity$MyAsyncTask#doInBackground@1
```

### Original stack trace

```
java.lang.RuntimeException: An error occured while executing doInBackground()
	at android.os.AsyncTask$3.done(AsyncTask.java:299)
	at java.util.concurrent.FutureTask$Sync.innerSetException(FutureTask.java:273)
	at java.util.concurrent.FutureTask.setException(FutureTask.java:124)
	at java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:307)
	at java.util.concurrent.FutureTask.run(FutureTask.java:137)
	at android.os.AsyncTask$SerialExecutor$1.run(AsyncTask.java:230)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1076)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:569)
	at java.lang.Thread.run(Thread.java:856)
Caused by: java.lang.NullPointerException
	at com.my.package.MainActivity$MyAsyncTask.doInBackground(MainActivity.java:254)
	at com.my.package.MainActivity$MyAsyncTask.doInBackground(MainActivity.java:1)
	at android.os.AsyncTask$2.call(AsyncTask.java:287)
	at java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:305)
	... 5 more
```
## Contributing

All contributions are welcome! If you wish to contribute, please create an issue first so that your feature, problem or question can be discussed.

## License

```
Copyright 2015 delight.im <info@delight.im>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
