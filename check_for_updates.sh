./gradlew dependencyUpdates -DoutputFormatter=html,json,xml || exit
echo "Opening results in Chrome:"
google-chrome build/reports/dependencyUpdates/report.html
