# Overview

This application features a dynamic form that allows users to submit building-related information. The form is built using Jetpack Compose, with data management handled through the MVVM architecture pattern.

# Functionality

- **Dynamic Form**: Loads form configuration from a JSON file and renders fields dynamically.
- **Form Fields**: Supports text fields and dropdown menus based on the configuration.
- **Form Submission**: Validates and submits form data, and clears fields upon successful submission.
- **Data Handling**: Uses a ViewModel to manage form state and validate inputs.

# Technologies

- **Jetpack Compose**: Modern toolkit for building native UIs on Android.
- **Room**: SQLite object-mapping library for database access.
- **ViewModel**: Architecture component for managing UI-related data.
- **LiveData & StateFlow**: Observable data holders for managing UI state.
- **Gson**: Library for parsing JSON data into objects.
- **Kotlin Coroutines**: For managing background tasks and asynchronous operations.
