# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PVPC-Android is an electricity price management application for Android that helps users optimize their appliance usage based on PVPC (Voluntary Price for Small Consumers) electricity rates in Spain. The app calculates the best time slots to run appliances based on real-time pricing data.

## Build & Test Commands

### Building
```bash
./gradlew assemble              # Build all variants
./gradlew assembleDebug         # Build debug variant
./gradlew assembleRelease       # Build release variant
```

### Testing
```bash
./gradlew test                  # Run all unit tests
./gradlew testDebugUnitTest     # Run unit tests for debug variant
./gradlew test --tests "*.ClassName"  # Run specific test class
```

### Code Quality
```bash
./gradlew ktlintCheck           # Check Kotlin code style
./gradlew ktlintFormat          # Auto-format Kotlin code
./gradlew detekt                # Run static code analysis
```

Detekt configuration is at `app/config/detekt/config.yml`.

## Architecture

### Clean Architecture Layers

The app follows **Clean Architecture** with clear separation of concerns:

```
ui/          → Jetpack Compose UI + ViewModels
domain/      → Use cases, models, strategies, error handling
data/        → Repositories, data sources (Room, Retrofit, DataStore)
di/          → Hilt dependency injection modules
```

### Key Architectural Patterns

**1. Domain Layer is Central**
- All business logic lives in `domain/usecase/` as single-responsibility use cases
- Use cases are injected into ViewModels via constructor injection
- Domain models (`Device`, `PVPCModel`, `Settings`, `TimeSlot`) are pure Kotlin data classes

**2. Repository Pattern**
- Interfaces in `data/` (e.g., `DeviceRepository`, `PriceRepository`, `SettingsRepository`)
- Implementations in `data/` (e.g., `DeviceRepositoryImpl`)
- Repositories coordinate between local (Room, DataStore) and remote (Retrofit) data sources

**3. Strategy Pattern for Calculations**
- `domain/strategy/BestTimeSlotCalculationStrategy` - Calculates optimal time slots for devices
- `domain/strategy/PriceCalculationStrategy` - Handles price computations
- Strategies are injected via Hilt in `di/StrategyModule.kt`

**4. Error Handling System**
- `domain/error/ErrorHandler` - Centralizes error processing
- `ErrorResult` sealed class with typed errors: `NetworkError`, `DataError`, `ValidationError`, `UnknownError`
- `handleErrors()` extension function for Flow to convert exceptions to error states
- All ViewModels use this system consistently

### UI Architecture

**Screen Structure:**
- Each feature has its own package under `ui/` (e.g., `ui/devices/`, `ui/home/`, `ui/settings/`)
- Screen composables are named `*Screen.kt` (e.g., `DevicesScreen.kt`, `HomeScreen.kt`)
- Each screen has a corresponding `*ViewModel.kt` and `*State.kt`

**State Management:**
- ViewModels expose `StateFlow<*State>` where State is a sealed class
- State classes have variants: `Loading`, `Success(data)`, `Error(message)`
- UI collects state with `collectAsStateWithLifecycle()`

**Navigation:**
- Single-activity architecture using `MainActivity`
- Navigation managed in `ui/main/MainScreen.kt` with bottom navigation
- Screen constants defined in `utils/Constants.kt`
- Current screens: `HOME_SCREEN`, `DEVICES_SCREEN`, `DEVICE_ADD_SCREEN`, `SETTINGS_SCREEN`

### Dependency Injection

Hilt modules in `di/`:
- `RepositoryModule` - Repository implementations
- `NetworkModule` - Retrofit, OkHttp, Moshi
- `LocalModule` - Room database, DataStore
- `CorotineModule` - CoroutineDispatchers (IO, Main, Default)
- `ErrorModule` - ErrorHandler and error-related dependencies
- `ValidationModule` - Validators (e.g., DateValidator)
- `StrategyModule` - Calculation strategies

All ViewModels use `@HiltViewModel` annotation.

### Data Persistence

**Room Database:**
- Entities in `data/local/db/model/` (e.g., `DeviceEntity`, `PVPCEntity`)
- DAOs in `data/local/db/dao/`
- Database class in `data/local/db/`

**DataStore (Proto):**
- Used for Settings persistence
- Proto definitions in `app/src/main/proto/`
- Accessed via `SettingsRepository`

**Network:**
- Retrofit service interfaces in `data/remote/`
- DTOs in `data/remote/dto/`
- Mappers in `data/mappers/` to convert DTOs to domain models

## Testing

### Test Structure

Tests are organized by layer:
```
test/java/com/codingpit/pvpcplanner/
  ├── domain/        # Use case, strategy, error handling tests
  ├── data/          # Repository, mapper tests
  ├── ui/            # ViewModel tests
  ├── fixtures/      # Test data fixtures
  └── integration/   # Integration tests
```

### Test Fixtures

Comprehensive test fixtures in `fixtures/`:
- `DeviceTestFixtures` - Common devices, edge cases, collections, scenarios
- `PVPCTestFixtures` - Price data for various scenarios
- `SettingsTestFixtures` - Settings configurations

Use fixtures instead of creating test data inline:
```kotlin
// Good
val device = DeviceTestFixtures.CommonDevices.WASHING_MACHINE

// Avoid
val device = Device(id = 1, name = "Test", hours = 2, icon = "icon")
```

### Testing Libraries

- JUnit 4 (not JUnit Platform/Jupiter)
- MockK for mocking
- Turbine for Flow testing
- Coroutines Test for testing suspend functions

### ViewModel Testing Pattern

ViewModels are tested by:
1. Mocking use case dependencies
2. Testing state transitions
3. Verifying use case invocations

Example structure:
```kotlin
class MyViewModelTest {
    private lateinit var viewModel: MyViewModel
    private val mockUseCase = mockk<MyUseCase>()

    @Before
    fun setup() {
        viewModel = MyViewModel(mockUseCase)
    }

    @Test
    fun `test state transitions`() = runTest {
        // Test implementation
    }
}
```

## Jetpack Compose Guidelines

### Component Organization

Private composable functions are preferred for screen-specific components:
```kotlin
@Composable
fun MyScreen() {
    // Public screen entry point
}

@Composable
private fun MyScreenHeader() {
    // Private component used only in MyScreen
}
```

### Modifier Patterns

Follow Material 3 design system:
- Use `MaterialTheme.colorScheme` for colors
- Use `MaterialTheme.typography` for text styles
- Corner radius typically 12dp or 16dp for cards
- Padding typically 16dp or 24dp horizontal

### State Handling

```kotlin
val state by viewModel.state.collectAsStateWithLifecycle()

when (val currentState = state) {
    is MyState.Loading -> LoadingIndicator()
    is MyState.Success -> SuccessContent(currentState.data)
    is MyState.Error -> ErrorMessage(currentState.error)
}
```

### UI Patterns

For detailed UI patterns and guidelines, see:
- [docs/guidelines/ui-patterns.md](docs/guidelines/ui-patterns.md) - General UI patterns
  - Screen layout patterns (Surface vs Scaffold)
  - Form input validation (decimal and integer numbers)
  - Input field units and labeling
  - Localization requirements
- [docs/guidelines/form-validation-and-modals.md](docs/guidelines/form-validation-and-modals.md) - Form validation and modals
  - Field-level validation with error states
  - Numeric field constraints (min/max values)
  - Modal dialog sizing (content-based vs full-screen)
  - Grid item layouts and alignment

Key points:
- Use `Surface` instead of `Scaffold` for most screens (consistency with existing screens)
- Always validate numeric input at the UI level with regex patterns
- Add field-level validation with error states for constrained fields (e.g., hours ≤ 24)
- Disable save/submit buttons when any field has validation errors
- Use modal dialogs instead of dropdowns for selection lists with 5+ items
- Use content-sized modals for short lists, full-screen for long lists
- Add clear unit labels (e.g., "W/h" for watts per hour)

## Design Integration

The project uses Pencil for design specifications. Design files are located at `/Users/juanje/Documents/pvpc-planner.pen`.

When implementing UI from designs:
1. Use the Pencil MCP tools to read design specifications
2. Match padding, spacing, corner radius, and colors exactly
3. Design system components are reusable and documented in the .pen file
4. Never use Read/Grep tools directly on .pen files - use MCP tools only

## Language & Localization

- Primary language: Spanish
- All user-facing strings in `res/values/strings.xml` (English) and `res/values-es/strings.xml` (Spanish)
- Always add strings to both files when adding new UI text
- String resources are preferred over hardcoded strings

## Git & CI

CI runs on every push via GitHub Actions (`.github/workflows/ci.yml`):
- Builds with `./gradlew assemble`
- Runs tests with `./gradlew test`
- Requires JDK 21

Main branch: `develop` (use this for PRs)

## Common Patterns

### Adding a New Feature Screen

1. Create package under `ui/yourfeature/`
2. Create `YourFeatureScreen.kt`, `YourFeatureViewModel.kt`, `YourFeatureState.kt`
3. Add screen constant to `utils/Constants.kt`
4. Add navigation logic to `ui/main/MainScreen.kt`
5. Update `ui/main/components/MainContent.kt` to include new screen
6. Create use cases in `domain/usecase/` if needed
7. Add strings to `res/values/strings.xml` and `res/values-es/strings.xml`
8. Write tests in corresponding test packages

### Adding a New Use Case

1. Create interface in `domain/usecase/YourUseCase.kt`
2. Implement with `operator fun invoke()` pattern
3. Inject dependencies via constructor
4. Add to DI module if repository/strategy is needed
5. Write unit tests in `test/java/.../domain/usecase/YourUseCaseTest.kt`
6. Add fixture data in `fixtures/` if needed

### Error Handling in Flows

Use the `handleErrors()` extension:
```kotlin
myFlow
    .map { /* transform */ }
    .handleErrors(errorHandler, "context_name", MyState.Factory)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MyState.Loading)
```

This automatically converts exceptions to error states using the centralized error system.
