# Form Validation and Modal Layout Guidelines

This document provides guidelines for implementing form validation, error handling, and modal dialogs in the PVPC-Android app.

## Form Input Validation

### Field-Level Validation

#### Numeric Field Constraints

When implementing numeric input fields with constraints (e.g., hours, watts, temperature), follow these patterns:

**1. Add error state to the UI state:**

```kotlin
data class Success(
    val fieldValue: String = "",
    val fieldError: String? = null,  // null means no error
    // ... other fields
) : MyState()
```

**2. Implement validation logic in ViewModel:**

```kotlin
private fun validateField() {
    updateSuccessState {
        val numericValue = fieldValue.toIntOrNull()
        val error = when {
            numericValue == null || numericValue <= 0 -> null // Basic validation
            numericValue > MAX_VALUE -> "error_validation_field_max"
            numericValue < MIN_VALUE -> "error_validation_field_min"
            else -> null
        }
        copy(fieldError = error)
    }
}
```

**3. Call validation on field change:**

```kotlin
fun onFieldChanged(value: String) {
    updateSuccessState { copy(fieldValue = value) }
    validateField()  // Validate immediately
    validateForm()   // Recheck overall form validity
}
```

**4. Include error state in form validation:**

```kotlin
private fun validateForm() {
    updateSuccessState {
        val isValid =
            requiredField.isNotEmpty() &&
            numericValue != null &&
            numericValue > 0 &&
            numericValue <= MAX_VALUE &&
            fieldError == null  // Important: check no field errors

        copy(saveEnabled = isValid)
    }
}
```

**5. Display error in UI:**

```kotlin
OutlinedTextField(
    value = fieldValue,
    onValueChange = onFieldChanged,
    isError = fieldError != null,
    supportingText = if (fieldError != null) {
        {
            Text(
                text = stringResource(
                    when (fieldError) {
                        "error_validation_field_max" -> R.string.error_validation_field_max
                        "error_validation_field_min" -> R.string.error_validation_field_min
                        else -> R.string.error_validation_generic
                    }
                ),
                color = MaterialTheme.colorScheme.error,
            )
        }
    } else null,
)
```

#### Example: Hours Field (Maximum 24)

From `DeviceAddScreen.kt`:

```kotlin
// State
data class Success(
    val hours: String = "",
    val hoursError: String? = null,
    // ...
)

// ViewModel validation
private fun validateHours() {
    updateSuccessState {
        val hoursValue = hours.toIntOrNull()
        val error = when {
            hoursValue == null || hoursValue <= 0 -> null
            hoursValue > 24 -> "error_validation_device_hours_max"
            else -> null
        }
        copy(hoursError = error)
    }
}

// Form validation includes error check
private fun validateForm() {
    updateSuccessState {
        val hoursValue = hours.toIntOrNull()
        val isValid =
            // ... other checks
            hoursValue != null &&
            hoursValue > 0 &&
            hoursValue <= 24 &&
            hoursError == null  // Disable save if error exists

        copy(saveEnabled = isValid)
    }
}
```

### Save Button State

**Always disable the save/submit button when:**
- Any required field is empty
- Any field has a validation error
- Numeric values are out of valid range
- The form is currently saving (to prevent double submission)

```kotlin
Button(
    onClick = onSaveClick,
    enabled = saveEnabled && !isSaving,  // Combine both conditions
) {
    if (isSaving) {
        CircularProgressIndicator(modifier = Modifier.size(18.dp))
    } else {
        Text(stringResource(R.string.save_action))
    }
}
```

## Modal Dialog Layouts

### Content-Sized Modals

When displaying modals with limited content (e.g., selection lists with few items), **do not force full-screen height**. Let the content determine the size.

#### Category Picker Example

**Good - Content wraps:**

```kotlin
Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),  // Vertical padding for spacing
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                // ...
            ) { /* ... */ }

            // Content grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
                // ...
            ) {
                items(categories) { /* ... */ }
            }
        }
    }
}
```

**Bad - Forced full screen:**

```kotlin
// ❌ DON'T DO THIS for short lists
Surface(
    modifier = Modifier.fillMaxSize(),  // Forces unnecessary height
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Content
    }
}
```

### Full-Screen Modals

Use full-screen modals for:
- Long lists (e.g., 20+ items)
- Complex selection interfaces
- Content that benefits from maximum space

#### Icon Picker Example

```kotlin
Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
) {
    Surface(
        modifier = Modifier.fillMaxSize(),  // OK for long icon list
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
    ) {
        // Content
    }
}
```

## Grid Item Layouts

### Equal Height Grid Items

When using `LazyVerticalGrid`, ensure all items in the same row have equal height for visual consistency.

#### Text Centering in Grid Items

**Icon items with text labels:**

```kotlin
Card(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),  // Fill column width
    // ...
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon.icon,
            modifier = Modifier.size(28.dp),
            // ...
        )
        Text(
            text = stringResource(icon.labelRes),
            fontSize = 11.sp,
            textAlign = TextAlign.Center,      // Center text horizontally
            modifier = Modifier.fillMaxWidth(),  // Ensure text uses full width
        )
    }
}
```

**Category items (text-only):**

```kotlin
Card(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),  // Fill column width
    // ...
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center,  // Center content in box
    ) {
        Text(
            text = stringResource(category.labelRes),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
        )
    }
}
```

### Grid Configuration

```kotlin
LazyVerticalGrid(
    columns = GridCells.Fixed(3),  // Or 2, depending on content
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
) {
    items(itemsList) { item ->
        ItemCard(
            item = item,
            modifier = Modifier.fillMaxWidth(),  // Each item fills column
            // ...
        )
    }
}
```

## String Resources for Errors

### Adding Error Messages

Always add error strings in both English and Spanish:

**`values/strings.xml`:**
```xml
<string name="error_validation_device_hours_max">Hours cannot be greater than 24.</string>
```

**`values-es/strings.xml`:**
```xml
<string name="error_validation_device_hours_max">Las horas no pueden ser mayores de 24.</string>
```

### Error String Naming Convention

Follow this pattern:
- `error_validation_[field]_[constraint]`

Examples:
- `error_validation_device_hours_max`
- `error_validation_device_hours_min`
- `error_validation_watts_positive`
- `error_validation_name_empty`

## Search and Filtering Patterns

### Implementing List Search/Filter

When adding search functionality to a list screen:

**1. Add search query to state:**

```kotlin
data class Success(
    val items: List<Item> = emptyList(),
    val searchQuery: String = "",
) : MyState() {
    val filteredItems: List<Item>
        get() = if (searchQuery.isBlank()) {
            items
        } else {
            items.filter { item ->
                item.name.contains(searchQuery, ignoreCase = true)
            }
        }
}
```

**2. Add search query state to ViewModel:**

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    // ... dependencies
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")

    val state = combine(
        dataFlow,
        searchQuery,
    ) { data, query ->
        MyState.Success(items = data, searchQuery = query)
    }
    // ... rest of flow setup

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }
}
```

**3. Create interactive SearchBar:**

```kotlin
@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.search_placeholder)) },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChanged("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.action_close),
                    )
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
    )
}
```

**4. Display "No Results" state:**

```kotlin
@Composable
private fun NoResultsState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.no_results_found),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
            )
        }
    }
}
```

**5. Use filtered list in UI:**

```kotlin
@Composable
private fun MyScreen_Success(
    state: MyState.Success,
    onSearchQueryChanged: (String) -> Unit,
) {
    val filteredItems = state.filteredItems

    LazyColumn {
        item {
            SearchBar(
                searchQuery = state.searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
            )
        }

        item {
            SectionHeader(itemCount = filteredItems.size)
        }

        if (filteredItems.isEmpty() && state.items.isNotEmpty()) {
            // Show "no results" when search returns nothing
            item { NoResultsState() }
        } else {
            items(filteredItems) { item ->
                ItemRow(item = item)
            }
        }
    }
}
```

### Search UX Best Practices

- **Real-time filtering**: Filter as the user types (no search button needed)
- **Clear button**: Show a clear/X button when search has text
- **Case-insensitive**: Use `contains(searchQuery, ignoreCase = true)`
- **Empty vs No Results**: Distinguish between:
  - Empty list (no items at all) → Show "No items" message
  - No search results (items exist but none match) → Show "No results found" message
- **Preserve all items**: Always keep the full list and filter from it
- **Focus management**: Clear keyboard focus when search is submitted
- **Item count**: Update section headers to show filtered count

### String Resources

Add search-related strings:

**`values/strings.xml`:**
```xml
<string name="search_placeholder">Search…</string>
<string name="no_results_found">No items found matching your search</string>
```

**`values-es/strings.xml`:**
```xml
<string name="search_placeholder">Buscar…</string>
<string name="no_results_found">No se encontraron elementos que coincidan con tu búsqueda</string>
```

## Summary Checklist

When implementing form fields with validation:

- [ ] Add error state property to UI state (`fieldError: String?`)
- [ ] Implement field-specific validation function in ViewModel
- [ ] Call validation on every field change
- [ ] Include error check in overall form validation
- [ ] Disable save button when any field has errors
- [ ] Display error message below field using `supportingText`
- [ ] Mark field as error with `isError = fieldError != null`
- [ ] Add error strings to both `strings.xml` files

When implementing modal dialogs:

- [ ] Use content-sized modals for short lists (< 10 items)
- [ ] Use full-screen modals for long lists (20+ items)
- [ ] Add appropriate padding for content-sized modals
- [ ] Use rounded corners for content-sized modals

When implementing grid layouts:

- [ ] Use `fillMaxWidth()` on grid items
- [ ] Center text with `TextAlign.Center` and alignment properties
- [ ] Ensure consistent spacing between items
- [ ] Test with different text lengths to verify alignment

When implementing search/filter functionality:

- [ ] Add `searchQuery: String` property to Success state
- [ ] Add computed `filteredItems` property that filters based on query
- [ ] Add `MutableStateFlow<String>` for search query in ViewModel
- [ ] Combine search query flow with data flow
- [ ] Add `updateSearchQuery()` function in ViewModel
- [ ] Convert search bar to `OutlinedTextField` with value and onChange
- [ ] Add leading search icon and trailing clear button (when text exists)
- [ ] Use `ImeAction.Search` for keyboard action
- [ ] Clear focus when search is submitted
- [ ] Show "No Results" state when filtered list is empty but original list is not
- [ ] Update section headers to show filtered count
- [ ] Add search-related strings to both `strings.xml` files
- [ ] Use case-insensitive filtering with `contains(query, ignoreCase = true)`
