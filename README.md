Clicker
=======

Simple clicker app for Android.

Usage
=====

To build server, execute `./gradlew build` (or `gradlew.bat build` for Windows) in Server directory. JDK 7 or higher is required.
The resulting jar will be in build/libs directory. Use `java -jar Server.jar [port] [max clients]` to run server and follow the instructions.

To build APK, execute `./gradlew build`  (or `gradlew.bat build` for Windows) in Android directory. You'll need JDK 6+ and Android SDK.
Copy app/build/apk/app-release-undigned.apk to your device and install it.
