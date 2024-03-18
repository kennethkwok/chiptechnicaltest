### Chip technical test
Author: Kenneth Kwok

### Acceptance criteria
- As a **user running the application** I can **select breed from the list** So that **I can view 
  pictures of that breed**
- **Scenario: Viewing the breed list** When I launch the app **Then** I see a list of dog breeds
- **Scenario: Viewing pictures of breed Given** I have launched the app **When** I select a breed 
  from the list **Then** I see 10 images of the breed

### Implementation
This codebase uses a single activity architecture and Jetpack Compose's navigation component to 
navigate between the list screen and the images screen. In terms of app architecture, the MVVM 
architectural pattern was used and utilises flows to communicate between the layers. 
Asynchronous tasks are launched with the use of coroutines. The repository pattern is also used to 
enable ViewModels stay responsible for business logic and view interactions only.

The codebase is structured by the following areas:
- `di` package contains all modules that are used for dependency injection. This is organised by 
  areas of responsibility (e.g. RepositoryModule contains all repositories that are provided, 
  and so on).
- `feature` package contains all views and viewModels to do with the main feature areas. This is 
  further split by screen. Views which are reused (e.g. DogBreedListItem) are placed in their 
  own file within the `items` package of the feature area.
- `network` package contains the API interface and associated network model objects.
- `repository` package contains the repository interface, the implementation and associated 
  model objects.
- `ui` package contains common views (e.g. LoadingIndicator), the navigation controller, as well as 
  the theme used in the app.
- `util` package contains all helper classes and class extensions.

###### Third party libraries
- Hilt for dependency injection
- Retrofit for network handling 
- OKHTTP3 logging for network logs
- Sandwich for modelling and handling Retrofit response and exceptions
- Coil for image loading
- Timber for general app debug logging
- Detekt for static code analysis

###### Third party libraries for unit testing
- MockK
- Turbine for testing Flows
- MockWebServer for testing HTTP clients

###### Third party libraries for UI tests
- Espresso

### Testing
JUnit tests have been written to test the ViewModel files, the repository, the API layer, as 
well as the class extensions in the `util` package. Note that the package structure mirrors the 
main app package structure for cleaner organisation and ease of maintenance.

Instrumented tests have also been written to test the E2E implementation of the app.