# GitHub Repositories App

An Android application that displays GitHub repositories using GitHub's REST API. 
The app showcases clean architecture principles, offline-first approach, and modern Android development practices.

### Features
* View GitHub repositories with pagination
* Offline support with local caching
* Network connectivity monitoring
* Repository details view

## Architecture
The app follows Clean Architecture principles with MVVM pattern:
```
app/
├── data/           # Data layer (Repository implementations, API, Database)
├── domain/         # Domain layer (Use cases, Domain models)
└── ui/             # Presentation layer (ViewModels, UI components)
```

### Key Components
- Repository Pattern: Manages data operations between remote and local sources
- Use Cases: Encapsulates business logic
- ViewModels: Manages UI state and business logic
- State Management: Uses Kotlin Flow for reactive state updates
- Dependency Injection: Hilt for dependency management
- Navigation: Single-activity architecture with Navigation component

## Tech Stack
* Kotlin
* Coroutines & Flow
* Hilt
* Retrofit 
* OkHttp 
* Kotlinx Serialization
* JUnit4 
* Mockk 
* Espresso
* Jetpack Components
    - Room
    - Navigation
    - Paging 3
    - ViewModel

## Getting Started
### Setup
1. Extract files from the zip
2. Open the project in Android Studio
3. Add your GitHub Personal Access Token:
   - Create local.properties in root directory if not exists
   - Add: GITHUB_TOKEN=your_personal_access_token
     - Note: GITHUB_TOKEN is not mandatory. 
     If no GITHUB_TOKEN is provided, GitHub API rate limits will be lower.
4. Build and run the app using Android Studio

### Testing
Run ```./gradlew app:testAll``` to run all unit and instruemented tests.
