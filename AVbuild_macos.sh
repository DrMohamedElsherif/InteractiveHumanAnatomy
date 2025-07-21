#!/bin/bash
set -e  # stop on errors

echo "Building project with Maven..."
mvn clean package -DskipTests

echo "Setting JAVA_HOME..."
export JAVA_HOME=$(/usr/libexec/java_home -v 24)
echo "JAVA_HOME set to $JAVA_HOME"

echo "Creating custom runtime image with jlink..."
$JAVA_HOME/bin/jlink \
  --module-path "$JAVA_HOME/jmods:$HOME/Downloads/javafx-jmods-24.0.2" \
  --add-modules java.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.web \
  --output "$HOME/anatomyviewer-build/custom-runtime" \
  --strip-debug --compress=2 --no-header-files --no-man-pages

echo "Packaging application with jpackage..."
$JAVA_HOME/bin/jpackage \
  --type dmg \
  --name AnatomyViewer \
  --input /Users/dr.elsherif/IdeaProjects/advanced-java-for-bioinformatics-2025-DrMohamedElsherif/target \
  --main-jar Assignments-1.0-SNAPSHOT.jar \
  --main-class Project02.AnatomyViewer \
  --dest /Users/dr.elsherif/IdeaProjects/advanced-java-for-bioinformatics-2025-DrMohamedElsherif/release/macOS \
  --vendor "Mohamed Elsherif" \
  --app-version 1.0 \
  --icon /Users/dr.elsherif/IdeaProjects/advanced-java-for-bioinformatics-2025-DrMohamedElsherif/src/main/resources/anatomy_icon.icns \
  --mac-package-identifier com.example.anatomyviewer \
  --mac-package-name AnatomyViewer \
  --runtime-image "$HOME/anatomyviewer-build/custom-runtime" \
  --java-options "--enable-native-access=javafx.graphics -Dprism.verbose=true" \
  --resource-dir /Users/dr.elsherif/javafx-sdk-24/lib \
  --verbose

echo "Build and packaging complete!"
