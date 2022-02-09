#!/bin/bash

echoError() {
  echo -e "\033[1;31m$1\033[0m"
}

echoSuccess() {
  echo -e "\033[1;32m$1\033[0m"
}

echoPending() {
  echo -e "\033[1;33m$1\033[0m"
}

echo "Ivy build verification started."
echoError "WARNING: Run on Android 9 or above. (Mockk works only on Android 9+)"
echoPending "Running Unit tests..."
./gradlew testDebugUnitTest || {
  echoError "IVY VERIFICATION: UNIT TESTING failed."
  exit
}
echoSuccess "IVY VERIFICATION: UNIT TESTS PASSED SUCCESSFULLY"
echoPending "Running UI tests..."
./gradlew connectedDebugAndroidTest --continue
echoPending "Instrumentation tests completed. Report opened in the default browser."
google-chrome "app/build/reports/androidTests/connected/index.html" || open "app/build/reports/androidTests/connected/index.html"
echoSuccess "AndroidTest results opened in the browser."