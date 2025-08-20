# PVPC Android - Code Quality TODOs

## Overview
This document contains actionable improvements for architecture, SOLID principles, and clean code practices based on comprehensive code analysis.

**Priority Legend:**
- 🔴 High Priority (Critical for production)
- 🟡 Medium Priority (Quality improvements) 
- 🟢 Low Priority (Nice to have)

---

## Architecture Improvements

### ✅ 1. Split Repository Interface (ISP Violation) - COMPLETED
- [x] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/data/Repository.kt`
- [x] **Action:** Split into `PriceRepository` and `DeviceRepository` interfaces
- [x] **Why:** Violates Interface Segregation Principle, clients forced to depend on methods they don't use
- [x] **Details:** Successfully split interface and updated all dependencies. All tests pass.

### ✅ 1.1. Split Repository Implementation (SRP, DIP) - COMPLETED
- [x] **Files:** `/app/src/main/java/com/codingpit/pvpcplanner/data/RepositoryImpl.kt`
- [x] **Action:** Split single implementation into `PriceRepositoryImpl` and `DeviceRepositoryImpl`
- [x] **Why:** Single Responsibility Principle - each class should have one reason to change
- [x] **Details:** Separated concerns, updated DI modules, maintained 100% test coverage with 11 tests

### 🔴 2. Extract Price Calculation Strategy (SRP, Strategy Pattern)
- [ ] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/ui/devices/DevicesViewModel.kt` (lines 54-70)
- [ ] **Action:** Extract `getBestSlot()` logic into separate `PriceCalculationStrategy` interface
- [ ] **Why:** Business logic mixed with presentation logic, hard to test and reuse
- [ ] **Details:** Move algorithm to domain layer with proper abstraction

### 🔴 3. Create Error Handling Strategy (DIP, Template Method Pattern)
- [ ] **Files:** Multiple ViewModels
- [ ] **Action:** Create `ErrorHandler` interface with implementations for different error types
- [ ] **Why:** Inconsistent error handling makes debugging difficult and user experience poor
- [ ] **Details:** Centralize error handling logic with consistent UI states

### 🟡 4. Implement Use Case for Complex Operations (SRP)
- [ ] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/ui/devices/DevicesViewModel.kt`
- [ ] **Action:** Create `CalculateBestTimeSlot` use case
- [ ] **Why:** Business logic should be in domain layer, not presentation layer
- [ ] **Details:** Extract complex calculation logic into testable use case

---

## SOLID Principle Fixes

### 🟡 5. Reduce ViewModel Dependencies (SRP, DIP)
- [ ] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/ui/home/HomeViewModel.kt` (lines 28-35)
- [ ] **Action:** Create `HomeUseCaseProvider` that encapsulates related use cases
- [ ] **Why:** Too many constructor parameters indicate class doing too much
- [ ] **Details:** 8+ dependencies suggest SRP violation

### 🟡 6. Extract Theme Management (SRP)
- [ ] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/ui/main/MainActivity.kt` (lines 44-50)
- [ ] **Action:** Create `ThemeManager` class to handle theme logic
- [ ] **Why:** MainActivity should focus on activity lifecycle, not theme management
- [ ] **Details:** Separate UI theme concerns from activity responsibilities

### 🟡 7. Create Specific Data Sources (ISP)
- [ ] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/data/local/sources/LocalDataSource.kt`
- [ ] **Action:** Split into `PriceLocalDataSource` and `DeviceLocalDataSource`
- [ ] **Why:** Different clients need different subsets of functionality
- [ ] **Details:** Apply ISP to data source layer

---

## Clean Code Refactoring

### 🔴 8. Extract Constants and Magic Numbers
- [ ] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/data/mappers/PvpcDtoMapper.kt` (line 20)
- [ ] **Action:** Extract `PRICE_CONVERSION_FACTOR = 1000` constant with documentation
- [ ] **Why:** Magic numbers make code unclear and error-prone
- [ ] **Details:** Document why division by 1000 is necessary

### 🟡 9. Create Date Utility Class (DRY Principle)
- [ ] **Files:** Multiple files with date manipulation
- [ ] **Action:** Create `DateFormatter` utility class with consistent date handling
- [ ] **Why:** Date logic scattered across multiple files, hard to maintain
- [ ] **Details:** Centralize date formatting and validation logic

### 🟡 10. Standardize Error Messages (DRY, Strategy Pattern)
- [ ] **Files:** Multiple ViewModels and use cases
- [ ] **Action:** Create `ErrorMessageProvider` with standardized error messages
- [ ] **Why:** Inconsistent error messages confuse users
- [ ] **Details:** Create localized, user-friendly error messages

### 🟡 11. Extract Validation Logic (SRP)
- [ ] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/domain/usecase/date/IsValidDate.kt`
- [ ] **Action:** Create `DateValidator` interface with implementation
- [ ] **Why:** Validation logic should be reusable and testable
- [ ] **Details:** Make validation logic more flexible and extensible

### 🟡 12. Improve Entity ID Generation (Clean Code)
- [ ] **File:** `/app/src/main/java/com/codingpit/pvpcplanner/data/mappers/PvpcDtoMapper.kt` (line 36)
- [ ] **Action:** Replace `day + startHour` string concatenation with proper ID generation
- [ ] **Why:** Current approach is fragile and could cause conflicts
- [ ] **Details:** Use UUID or more robust ID generation strategy

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
- [ ] **File:** Expand `/app/src/androidTest/java/com/codingpit/pvpcplanner/ExampleInstrumentedTest.kt`
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

❌ **Areas for Improvement:**
- Interface Segregation Principle violations
- Business logic mixed with presentation layer
- Inconsistent error handling
- Limited test coverage
- Magic numbers and scattered constants
- Over-complex ViewModels with too many dependencies

## Progress Tracking

- [ ] **Phase 1:** High Priority Items (1-3, 8, 16) - Critical for production
- [ ] **Phase 2:** Medium Priority Items (4-7, 9-13, 17-18) - Quality improvements
- [ ] **Phase 3:** Low Priority Items (14-15, 19-23) - Nice to have

**Last Updated:** 2025-08-20
**Total TODOs:** 24 (added 1.1 - Repository Implementation Split)
**Completed:** 2/24 (8%)