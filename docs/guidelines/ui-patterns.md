# UI Patterns & Guidelines

This document contains UI patterns and design decisions for the PVPC-Android application.

## Screen Layout Patterns

### Surface vs Scaffold

**Guideline:** Use `Surface` for screen containers instead of `Scaffold` unless a screen specifically needs floating action buttons or persistent navigation elements.

**Reasoning:**
- Maintains consistency across the app
- `Surface` provides a simpler, cleaner layout structure
- Most screens in the app (HomeScreen, SettingsScreen, DevicesScreen) use `Surface`
- TopAppBar navigation should be integrated directly into the screen content

**Example:**
```kotlin
@Composable
fun MyScreen(onBackClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Header with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.action_back),
                    )
                }
                Text(
                    text = stringResource(R.string.screen_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Screen content...
        }
    }
}
```

## Form Input Patterns

### Number Input Validation

**Guideline:** Always validate numeric input at the UI level to prevent invalid characters.

#### Decimal Numbers (Float)
Use for consumption, prices, or any measurement that requires decimal precision:

```kotlin
OutlinedTextField(
    value = watts,
    onValueChange = { newValue ->
        // Normalize comma to dot for validation and parsing
        val normalized = newValue.replace(',', '.')
        // Only allow valid decimal numbers
        if (newValue.isEmpty() || normalized.matches(Regex("^\\d*\\.?\\d*$"))) {
            onWattsChange(normalized)
        }
    },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
)
```

**Regex Explanation:**
- `^` - Start of string
- `\d*` - Zero or more digits
- `\.?` - Optional decimal point
- `\d*` - Zero or more digits after decimal
- `$` - End of string

#### Integer Numbers
Use for hours, counts, or whole numbers:

```kotlin
OutlinedTextField(
    value = hours,
    onValueChange = { newValue ->
        // Only allow integers
        if (newValue.isEmpty() || newValue.matches(Regex("^\\d+$"))) {
            onHoursChange(newValue)
        }
    },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
)
```

**Regex Explanation:**
- `^` - Start of string
- `\d+` - One or more digits
- `$` - End of string

### Input Field Units

**Guideline:** Use clear, descriptive units that indicate rate when applicable.

**Examples:**
- Power (instantaneous): `W`
- Energy (consumption over time): `Wh` or `kWh`
- Duration: `hours` or localized equivalent
- Speed: `km/h`, `m/s`, etc.

```kotlin
OutlinedTextField(
    value = watts,
    onValueChange = onWattsChange,
    suffix = { Text("W") }, // Clear power indication
)
```

## Modal Selection Patterns

### Picker Modals

**Guideline:** Use full-screen modal dialogs for selection lists instead of dropdown menus when:
- The list has 5+ items
- Items need visual representation (icons, colors)
- You want to provide a better user experience on mobile
- Consistency with other selection patterns in the app

**Example Structure:**

```kotlin
@Composable
private fun ItemPickerModal(
    items: List<ItemType>,
    selectedItem: ItemType,
    onItemSelected: (ItemType) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.select_item),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.action_close))
                    }
                }

                // Grid of items
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // Adjust based on content
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(items) { item ->
                        ItemPickerCard(
                            item = item,
                            isSelected = selectedItem.id == item.id,
                            onClick = { onItemSelected(item) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemPickerCard(
    item: ItemType,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Item content (icon, text, etc.)
            Text(
                text = stringResource(item.labelRes),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor,
            )
        }
    }
}
```

### State Management for Modals

**ViewModel:**
```kotlin
fun showItemPicker() {
    updateSuccessState { copy(showItemPicker = true) }
}

fun hideItemPicker() {
    updateSuccessState { copy(showItemPicker = false) }
}

fun onItemSelected(item: ItemType) {
    updateSuccessState {
        copy(
            selectedItem = item,
            showItemPicker = false, // Auto-close on selection
        )
    }
}
```

**State:**
```kotlin
data class Success(
    val selectedItem: ItemType? = null,
    val availableItems: List<ItemType> = emptyList(),
    val showItemPicker: Boolean = false,
) : MyState()
```

## Localization

**Guideline:** Always add strings to both `values/strings.xml` (English) and `values-es/strings.xml` (Spanish).

When adding new UI text:
1. Add to English strings first
2. Immediately add Spanish translation
3. Use descriptive string keys (e.g., `edit_device_title` not `title2`)
4. Group related strings together

## Testing Patterns

When adding new UI patterns:
1. Verify input validation with edge cases (empty, max values, special characters)
2. Test modal interactions (open, close, select, dismiss)
3. Ensure state persistence through configuration changes
4. Test with different screen sizes and orientations

## Related Files

- DeviceAddScreen.kt - Example of Surface-based form screen with modals
- DevicesScreen.kt - Example of Surface-based list screen
- HomeScreen.kt - Example of Surface-based content screen
