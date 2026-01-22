# PVPC Android - Code Quality TODOs

## Overview

This document contains actionable improvements for architecture, SOLID principles, and clean code
practices based on comprehensive code analysis.

**Priority Legend:**

- 🔴 High Priority (Critical for production)
- 🟡 Medium Priority (Quality improvements)
- 🟢 Low-Priority (Nice-to-have)

---

## Architecture Improvements

### ✅ 1. Split Repository Interface (ISP Violation) - COMPLETED

- [x] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/data/Repository.kt`
- [x] **Action:** Split into `PriceRepository` and `DeviceRepository` interfaces
- [x] **Why:** Violates Interface Segregation Principle, clients forced to depend on methods they
  don't use
- [x] **Details:** Successfully split interface and updated all dependencies. All tests pass.

### ✅ 1.1. Split Repository Implementation (SRP, DIP) - COMPLETED

- [x] **Files:** `/app/src/main/java/com/codingpit/pvpcplanner/data/RepositoryImpl.kt`
- [x] **Action:** Split single implementation into `PriceRepositoryImpl` and `DeviceRepositoryImpl`
- [x] **Why:** Single Responsibility Principle - each class should have one reason to change
- [x] **Details:** Separated concerns, updated DI modules, maintained 100% test coverage with 11
  tests

### ✅ 2. Extract Price Calculation Strategy (SRP, Strategy Pattern) - COMPLETED

- [x] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/ui/devices/DevicesViewModel.kt` (lines
  54-70)
- [x] **Action:** Extract `getBestSlot()` logic into separate `PriceCalculationStrategy` interface
- [x] **Why:** Business logic mixed with presentation logic, hard to test and reuse
- [x] **Details:** Created strategy pattern with `BestTimeSlotCalculationStrategy`,
  `CalculateBestTimeSlot` use case, and comprehensive test coverage (24 tests passing)

### ✅ 3. Create Error Handling Strategy (DIP, Template Method Pattern) - COMPLETED

- [x] **Files:** Multiple ViewModels
- [x] **Action:** Create `ErrorHandler` interface with implementations for different error types
- [x] **Why:** Inconsistent error handling makes debugging difficult and user experience poor
- [x] **Details:** Successfully implemented centralized error handling with `ErrorHandler`
  interface, `DefaultErrorHandler` implementation, centralized error state management, and updated
  all ViewModels to use the new strategy. Added proper dependency injection.

### ✅ 4. Implement Use Case for Complex Operations (SRP) - COMPLETED

- [x] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/ui/devices/DevicesViewModel.kt`
- [x] **Action:** Create `CalculateBestTimeSlot` use case
- [x] **Why:** Business logic should be in domain layer, not presentation layer
- [x] **Details:** Extracted complex calculation logic into testable use case - completed as part of
  the Price Calculation Strategy task

---

## SOLID Principle Fixes

### ✅ 5. Reduce ViewModel Dependencies (SRP, DIP) - COMPLETED

- [x] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/ui/home/HomeViewModel.kt` (lines
  28-35)
- [x] **Action:** Create `HomeUseCaseProvider` that encapsulates related use cases
- [x] **Why:** Too many constructor parameters indicate class doing too much
- [x] **Details:** Created `HomeUseCaseProvider` to group use cases and refactored ViewModel and
  Test.

### ✅ 6. Extract Theme Management (SRP) - COMPLETED

- [x] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/ui/main/MainActivity.kt` (lines 44-50)
- [x] **Action:** Create `ThemeManager` class to handle theme logic
- [x] **Why:** MainActivity should focus on activity lifecycle, not theme management
- [x] **Details:** Created `ThemeManager` in `ui.theme` package and refactored `MainActivity` to use
  it.

### ✅ 7. Create Specific Data Sources (ISP) - COMPLETED

- [x] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/data/local/sources/LocalDataSource.kt`
- [x] **Action:** Split into `PriceLocalDataSource` and `DeviceLocalDataSource`
- [x] **Why:** Different clients need different subsets of functionality
- [x] **Details:** Created `PriceLocalDataSource` and `DeviceLocalDataSource`, refactored
  repositories and modules to use them, and updated all tests. Removed old `LocalDataSource`.

---

## Clean Code Refactoring

### ✅ 8. Extract Constants and Magic Numbers - COMPLETED

- [x] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/data/mappers/PvpcDtoMapper.kt` (line
    20)
- [x] **Action:** Extract `PRICE_CONVERSION_FACTOR = 1000` constant with documentation
- [x] **Why:** Magic numbers make code unclear and error-prone
- [x] **Details:** Created `MappingConstants` class with documented constants. Extracted
  `PRICE_CONVERSION_FACTOR`, `ID_SEPARATOR`, and decimal separator constants.

### ✅ 9. Create Date Utility Class (DRY Principle) - COMPLETED

- [x] **Files:** Multiple files with date manipulation
- [x] **Action:** Create `DateFormatter` utility class with consistent date handling
- [x] **Why:** Date logic scattered across multiple files, hard to maintain
- [x] **Details:** Created centralized `DateFormatter` utility with comprehensive date handling,
  timezone management, and formatting functions. Updated `DateChecker`, `GetPrices`, and date use
  cases to use the utility.

### ✅ 10. Standardize Error Messages (DRY, Strategy Pattern) - COMPLETED

- [x] **Files:** Multiple ViewModels and use cases
- [x] **Action:** Create `ErrorMessageProvider` with standardized error messages
- [x] **Why:** Inconsistent error messages confuse users
- [x] **Details:** Created `ErrorMessageProvider` interface with `DefaultErrorMessageProvider`
  implementation. Provides consistent, localized Spanish error messages for network, data,
  validation, and generic errors. Includes dependency injection setup.

### ✅ 11. Extract Validation Logic (SRP) - COMPLETED

- [x] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/domain/usecase/date/IsValidDate.kt`
- [x] **Action:** Create `DateValidator` interface with implementation
- [x] **Why:** Validation logic should be reusable and testable
- [x] **Details:** Created `DateValidator` interface with `DefaultDateValidator` implementation.
  Provides flexible validation with detailed error results including error codes and user-friendly
  messages. Updated `IsValidDate` use case to use the new validator.

### ✅ 12. Improve Entity ID Generation (Clean Code) - COMPLETED

- [x] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/data/mappers/PvpcDtoMapper.kt` (line
    36)
- [x] **Action:** Replace `day + startHour` string concatenation with proper ID generation
- [x] **Why:** Current approach is fragile and could cause conflicts
- [x] **Details:** Replaced string concatenation with proper formatted ID using `ID_SEPARATOR`
  constant. Changed from `day + startHour` to `"${day}${ID_SEPARATOR}${startHour}"` for better
  readability and maintainability.

---

## Performance Optimizations

### 🟡 13. Implement Caching Strategy (Strategy Pattern)

- [ ] **Files:** Repository implementations
- [ ] **Action:** Add cache layer with TTL for price data
- [ ] **Why:** Reduce API calls and improve user experience
- [ ] **Details:** Implement in-memory cache with time-based expiration

### 🟢 14. Optimize Database Queries (Performance)

- [ ] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/data/local/db/PVPCDao.kt`
- [ ] **Action:** Add indexes on frequently queried columns (day, date)
- [ ] **Why:** Improve query performance as data grows
- [ ] **Details:** Add database migrations for index creation

### 🟢 15. Implement Data Pagination (Performance)

- [ ] **Files:** Repository and DAO classes
- [ ] **Action:** Add pagination for historical price data
- [ ] **Why:** Prevent memory issues with large datasets
- [ ] **Details:** Use Android Paging 3 library for efficient data loading

---

## Testing Improvements

### 🔴 16. Create Repository Tests (Testing)

- [ ] **File:** Create `/app/src/test/java/com/codingpit/pvpcplanner/data/RepositoryImplTest.kt`
- [ ] **Action:** Add comprehensive unit tests for repository implementations
- [ ] **Why:** No repository tests exist despite business logic complexity
- [ ] **Details:** Mock dependencies and test all repository methods

### 🟡 17. Add Use Case Tests (Testing)

- [ ] **Files:** Create test files for each use case
- [ ] **Action:** Test all use cases with mocked dependencies
- [ ] **Why:** Use cases contain business logic that must be tested
- [ ] **Details:** Test happy path and error scenarios

### 🟡 18. Create ViewModel Tests (Testing)

- [ ] **Files:** Create test files for each ViewModel
- [ ] **Action:** Test ViewModel state management and user interactions
- [ ] **Why:** ViewModels coordinate application flow and need testing
- [ ] **Details:** Test state changes and error handling

### 🟢 19. Add Integration Tests (Testing)

- [ ] **File:** Expand
  `/app/src/androidTest/java/com/codingpit/pvpcplanner/ExampleInstrumentedTest.kt`
- [ ] **Action:** Add tests for database operations and API integration
- [ ] **Why:** Verify component integration works correctly
- [ ] **Details:** Test end-to-end workflows

### 🟢 20. Mock Network Responses (Testing)

- [ ] **Files:** Test files for remote data sources
- [ ] **Action:** Create test fixtures and mock API responses
- [ ] **Why:** Enable testing without network dependency
- [ ] **Details:** Use MockWebServer or similar for API testing

---

## Documentation Needs

### 🟢 21. Add API Documentation (Documentation)

- [ ] **Files:** All public interfaces and classes
- [ ] **Action:** Add KDoc documentation for public APIs
- [ ] **Why:** Improve code maintainability and developer onboarding
- [ ] **Details:** Document parameters, return values, and exceptions

### 🟢 22. Document Architecture Decisions (Documentation)

- [ ] **File:** Create architectural decision records (ADRs)
- [ ] **Action:** Document why Clean Architecture was chosen and key patterns used
- [ ] **Why:** Help future developers understand architectural decisions
- [ ] **Details:** Create `docs/architecture/` directory with ADR files

### 🟢 23. Create Development Setup Guide (Documentation)

- [ ] **Action:** Document development environment setup and build process
- [ ] **Why:** Streamline developer onboarding
- [ ] **Details:** Include IDE setup, build configurations, and testing guidelines

---

## Current Architecture Summary

✅ **Strengths:**

- Clean Architecture implementation with proper layering
- Modern Android development stack (Compose, Hilt, Room, Retrofit)
- Good separation of concerns between layers
- Proper dependency injection setup
- Repository and Use Case patterns implemented

❌ **Remaining Areas for Improvement:**

- Limited test coverage (in progress)
- Over-complex ViewModels with too many dependencies
- Some SOLID principle improvements pending (data source layer)
- Performance optimizations not yet implemented

✅ **Improvements Completed:**

- Interface Segregation Principle fixes (Repository split)
- Business logic extracted from presentation layer (Strategy pattern)
- Magic numbers and constants centralized
- Date handling utilities centralized
- Validation logic extracted and made flexible
- Error message standardization implemented
- Entity ID generation improved

## Progress Tracking

- [x] **Phase 1:** High Priority Items (1-2, 8, 16) - Critical for production (3/4 completed)
- [x] **Phase 2:** Medium-Priority Items (9-12, 4-7, 13, 17-18) - Quality improvements (8/11
  completed)
- [ ] **Phase 3:** Low-Priority Items (14-15, 19-23) - Nice-to-have

**Last Updated:** 2026-01-21
**Total TODOs:** 24 (added 1.1 - Repository Implementation Split)
**Completed:** 13/24 (54%)
