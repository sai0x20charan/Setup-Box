package com.charan.setupBox.presentation.home.components

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.charan.setupBox.data.local.entity.SetupBoxContent

@Composable
fun ListRow(
    items: List<SetupBoxContent>,
    onClick: (item: SetupBoxContent) -> Unit,
    shouldRequestFocus: Boolean = false,
) {
    val focusRequester = remember { FocusRequester() }

    LazyRow {
        items(items.size) { index ->
            if (shouldRequestFocus) {
                LaunchedEffect(key1 = Unit) {
                    focusRequester.requestFocus()
                }
            }
            CardItem(
                item = items[index],
                modifier = Modifier.then(
                    if (shouldRequestFocus && index == 0) {
                        Modifier.focusRequester(focusRequester)
                    } else Modifier
                ),
                onClick = {
                    onClick(items[index])
                }
            )
        }
    }


}