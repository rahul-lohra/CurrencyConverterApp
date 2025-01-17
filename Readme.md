# Currency Converter Android App

This Currency Converter app provides real-time exchange rate data to help users quickly convert between different currencies. The app is built with Kotlin and follows modern Android development practices, employing the following

1. Clean Architecture
2. MVVM
3. Jetpack Compose
4. State driven and Uni directional architecture

It is designed to be scalable, testable, and easy to maintain.

## Architecture Overview

The app is structured around **Clean Architecture** principles, separating concerns into **Presentation**, **Domain**, and **Data** layers. This approach ensures a clear separation of responsibilities, making the codebase maintainable and testable.

- **Presentation Layer**: Built with Jetpack Compose and follows MVVM. It handles UI events, states, and data presentation. To enhance scalability and readability, the main activity's Jetpack Compose is split into multiple Compose functions such as picker and grid modules. Instead of using a single, monolithic ViewModel, separate ViewModels are used for each module, each acting as a dedicated state holder.

    -  **Benefits of Using Separate ViewModels:**
  - **Separation of Concerns**: Each ViewModel manages only the state and logic relevant to its corresponding UI module, improving maintainability.
  - **Improved Scalability**: Enables easier extension and modification of individual modules without affecting others.
  - **Enhanced Readability**: Focused ViewModels reduce complexity, making the code more understandable.
  - **Better Testability**: Independent ViewModels allow for isolated and reliable testing.
  - **Easier State Management**: Managing state becomes simpler, reducing risks of shared state conflicts.

  - ### State-Driven and Uni-Directional Architecture
    
    The app follows a **state-driven** and **uni-directional data flow** architecture. Each screen is powered by a ViewModel that acts as a state holder, exposing `StateFlow` objects to the UI. This ensures that:
    
    - **State-Driven UI**: The UI reacts to changes in the state emitted by the ViewModel, ensuring consistency and predictability in the app's behavior.
    - **Uni-Directional Data Flow**: All data flows in a single direction — user actions are captured by the UI, sent to the ViewModel, and any state changes flow back to the UI. This approach reduces complexity, making the app easier to debug and maintain.
   
  - ### Shared State Management
  - The app employs a shared state management approach, where multiple ViewModels share common state holders to manage data that needs to be accessed or modified across different UI components. These shared states are implemented in the rahul.lohra.currencyconverter.ui.states.shared package like:
  - BaseCurrencyStateHolder: Manages the base currency state and allows different ViewModels to access or update it. 
  - CurrencyExchangeSharedState: Holds the state of currency exchange rates, enabling consistent data sharing across multiple ViewModels.

    - ### This shared state mechanism ensures:
    - Consistency: Shared data is always up-to-date across different parts of the app.
    - Reduced Redundancy: Common states are maintained in a single source of truth, avoiding duplication of logic across ViewModels.
    - Scalability: As the app grows, adding new features or ViewModels that rely on the same shared state is straightforward.
    By using this shared state pattern, the app achieves a more organized, maintainable, and cohesive architecture.

- **Domain Layer**: Contains business logic and use cases, ensuring the core functionality is independent of data sources.
- **Data Layer**: Manages data retrieval from remote (Retrofit) and local (Network Cache) sources and implements caching strategies.

### Benefits of the Architecture
- **Separation of Concerns**: Each layer is responsible for a distinct aspect of the app, making the code easier to manage.
- **Testability**: The architecture facilitates unit testing by isolating business logic in the Domain layer.
- **Scalability**: New features can be added with minimal changes to existing code.

## Core Technologies and Libraries Used
- **Kotlin**: The programming language used.
- **Jetpack Compose**: For building the UI.
- **Coroutines/Flow**: For asynchronous programming.
- **Retrofit & OkHttp**: For networking.
- **MockWebServer**: For testing network interactions.
- **JUnit & MockK**: For unit testing.

## Testing Methodology

We follow a comprehensive testing strategy to ensure the app's reliability:

- **Unit Testing**: JUnit and MockK are used to test ViewModels, Use Cases, and Repositories.
- **Integration Testing**: MockWebServer is employed to simulate API responses for testing Retrofit integrations.
- **UI Testing**: Compose Testing is used to verify UI behavior.
- **Code Coverage**: We maintain high code coverage to ensure that the core functionality is thoroughly tested.

## Project Structure

```bash
src/
 ├── main/
 │   ├── java/
 │   │   ├── data/          # Data sources, repositories, and networking code
 │   │   ├── domain/        # Use cases, business logic
 │   │   ├── ui/            # ViewModels and UI components (Jetpack Compose)
 │   │   └── di/            # Dependency injection setup with Hilt
 │   └── res/               # UI resources such as XML layouts
 └── test/                  # Unit tests
#   C u r r e n c y C o n v e r t e r A p p  
 