SmartPantry — Android Mobile Application

 Project Overview
SmartPantry is an Android mobile application designed to help users manage pantry ingredients and discover recipes based on the ingredients they have available. The application helps users track ingredient quantities and identify recipes they can prepare.

 Main Features
- Add and manage pantry ingredients.
- Store ingredient information, including quantities and units.
- Match available pantry ingredients with recipe requirements.
- Display suggested recipes based on available ingredients.
- View recipe details and required ingredients.
- Manage application settings.

 Technologies Used
- Programming Language: Java
- Development Environment: Android Studio
- Database: SQLite (local database)
- User Interface: Android XML layouts and Java activities
- Version Control: Git and GitHub

 Project Structure
- `app/` — Application source code and resources.
- `gradle/` — Gradle wrapper support files.
- `build.gradle.kts` — Project build configuration.
- `settings.gradle.kts` — Gradle project settings.
- `gradlew` and `gradlew.bat` — Gradle wrapper scripts.

 How to Open the Project
1. Download or clone this repository.
2. Open Android Studio.
3. Select **Open** and choose the project folder containing `settings.gradle.kts`.
4. Allow Gradle to synchronise and resolve the project dependencies.
5. Run the application on an Android emulator or compatible Android device.

 Testing
The recipe-matching functionality was tested using different pantry quantities. Tests included checking that a recipe appears when sufficient ingredients are available, disappears when quantities are insufficient, and becomes available again when the required quantities are restored. Additional ingredients were also added to verify recipe suggestions.

 Author
Nikaylin Moodley

Student Number: 402306801
