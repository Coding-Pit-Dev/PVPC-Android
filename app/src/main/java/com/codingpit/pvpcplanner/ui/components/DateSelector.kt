package com.codingpit.pvpcplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codingpit.pvpcplanner.R

@Composable
fun DateSelector(
    modifier: Modifier = Modifier,
    selectedDate: String,
    currentDate: String,
    nextDayEnabled: Boolean,
    onPreviewClicked: () -> Unit,
    onNextClicked: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .padding(16.dp)
                .background(
                    MaterialTheme.colorScheme.background,
                    RoundedCornerShape(8.dp),
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
    ) {
        IconButton(modifier = Modifier, onClick = onPreviewClicked) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = stringResource(R.string.a11y_previous_day)
            )
        }
        val selectedDateText =
            if (selectedDate == currentDate) {
                stringResource(R.string.current_date_template, selectedDate)
            } else {
                selectedDate
            }
        Text(selectedDateText)
        IconButton(modifier = Modifier, enabled = nextDayEnabled, onClick = onNextClicked) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                contentDescription = stringResource(R.string.a11y_next_day),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DateSelector_Preview() {
    DateSelector(
        modifier = Modifier.fillMaxWidth(),
        selectedDate = "2023-09-01",
        nextDayEnabled = true,
        onPreviewClicked = { },
        onNextClicked = { },
        currentDate = "2023-09-01",
    )
}
