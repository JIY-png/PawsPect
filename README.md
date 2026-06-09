# Pawspect - On-Device Dog Breed Classifier

Pawspect is a native Android application that allows users to capture a photo of a dog or select one from their gallery, and immediately classify its breed using an on-device TensorFlow Lite model. The classification is completely offline, ensuring user privacy and instant predictions without requiring any network calls.

---

## Features
* **Offline On-Device Inference**: Utilizes a TensorFlow Lite model trained on the **Stanford Dogs Dataset** to identify 120 breeds.
* **Dual Image Input**: Capture new photos using **CameraX** or pick existing ones from the device gallery.
* **Top-3 Predictions**: Displays the three most likely breeds with dynamic progress bars indicating confidence levels.
* **Modern Material 3 Adaptive Layout**: Dynamic design using Jetpack Compose that optimizes UI elements for phone and larger screen formats.

---

## Tech Stack
* **Language**: Kotlin
* **UI**: Jetpack Compose (Material 3 Adaptive Layouts)
* **Image Loading**: Coil
* **Camera Handling**: CameraX
* **Machine Learning**: TensorFlow Lite (`Interpreter`)

---

## Source Code Structure

Here are the key source files implementing the application features:

### Core Architecture & Business Logic
* **[MainActivity.kt](file:///C:/Users/Admin/AndroidStudioProjects/Pawspect/app/src/main/java/com/example/pawspect/MainActivity.kt)**: Entry point of the application. It initializes edge-to-edge configurations and sets up the root Jetpack Compose Navigation.
* **[DogBreedClassifier.kt](file:///C:/Users/Admin/AndroidStudioProjects/Pawspect/app/src/main/java/com/example/pawspect/ml/DogBreedClassifier.kt)**: Houses the TensorFlow Lite initialization, image preprocessing (scaling to 224x224 and [0.0, 1.0] float normalization), inference execution, and sorting of top-3 breeds.
* **[DogBreedRepository.kt](file:///C:/Users/Admin/AndroidStudioProjects/Pawspect/app/src/main/java/com/example/pawspect/data/repository/DogBreedRepository.kt)**: Bridges the UI view models with the classifier by loading image URIs into memory as bitmaps and calling the classification engine asynchronously on `Dispatchers.IO`.

### UI Screens & State Management
* **[HomeScreen.kt](file:///C:/Users/Admin/AndroidStudioProjects/Pawspect/app/src/main/java/com/example/pawspect/ui/screens/HomeScreen.kt)**: Offers the option to capture an image using CameraX or select an image from the gallery, featuring an interactive image preview.
* **[ResultsScreen.kt](file:///C:/Users/Admin/AndroidStudioProjects/Pawspect/app/src/main/java/com/example/pawspect/ui/screens/ResultsScreen.kt)**: Displays the analyzed image along with a list of the top-3 predicted dog breeds and their corresponding confidence scores.
* **[HomeViewModel.kt](file:///C:/Users/Admin/AndroidStudioProjects/Pawspect/app/src/main/java/com/example/pawspect/ui/viewmodel/HomeViewModel.kt)**: Manages states related to image capture, selection, and overall UI navigation.
* **[DogBreedResponse.kt](file:///C:/Users/Admin/AndroidStudioProjects/Pawspect/app/src/main/java/com/example/pawspect/data/model/DogBreedResponse.kt)**: Contains data classes for modeling prediction data (`BreedPrediction` and `DogBreedResponse`).

---

## How to Install and Run the App

Follow these step-by-step instructions to compile and deploy Pawspect onto your Android device or emulator.

### Prerequisites
1. **Android Studio**: Ensure you have Android Studio Koala (or newer) installed.
2. **Java Development Kit (JDK)**: JDK 17 is required (bundled automatically inside modern Android Studio installations).
3. **Android SDK & Device**: An emulator or physical Android device running Android 7.0 (API level 24) or higher.

### Step 1: Open the Project in Android Studio
1. Launch **Android Studio**.
2. Click **Open** (or **File > Open**).
3. Navigate to and select the directory: `C:\Users\Admin\AndroidStudioProjects\Pawspect`
4. Click **OK** to open the project.

### Step 2: Sync Gradle and Download Dependencies
1. Android Studio will automatically start index files and download Gradle dependencies defined in the project build configuration.
2. Wait for the sync to complete. You should see a green checkmark or build success message in the status bar at the bottom.
3. If prompt asks to update build tools, you may accept or skip.

### Step 3: Configure Target Device
* **To run on an Emulator**:
  1. Open the **Device Manager** in Android Studio (top-right side panel).
  2. Start an emulator (e.g., Pixel 8 API 34).
* **To run on a Physical Device**:
  1. Enable **Developer Options** and **USB Debugging** on your Android phone.
  2. Connect the phone to your computer via a USB cable.
  3. Allow USB debugging authorization if prompted on the device.

### Step 4: Run the App
1. Locate the toolbar at the top of Android Studio.
2. Confirm that the run configuration drop-down lists `app` and that your target device is selected.
3. Click the green **Run** button (play icon) or press `Shift + F10` (Windows).
4. Gradle will compile the source code, pack the resources, package the APK, and install it on the destination device automatically.
