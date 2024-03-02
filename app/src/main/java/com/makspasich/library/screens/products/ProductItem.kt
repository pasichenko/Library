package com.makspasich.library.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.makspasich.library.model.Product
import com.makspasich.library.model.State
import com.makspasich.library.model.TagName
import com.makspasich.library.ui.theme.LibraryTheme
import com.makspasich.library.toText
import com.makspasich.library.util.shortKey

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductItem(product: Product, onClick: () -> Unit) {
    CustomBadgedBox(
        modifier = Modifier.fillMaxWidth(),
        badge = {
            Badge { Text(text = product.state.toText()) }
        }
    ) {
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onClick() },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
//                        .width(70.dp) //for 4- or 5-digit key
                            .width(60.dp) //for 3-digit key
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = product.shortKey(), maxLines = 1)
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentHeight(Alignment.CenterVertically),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = product.name.toString()
                    )
                }
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Max),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Equalizer, contentDescription = null)
                    Text(modifier = Modifier.width(50.dp), text = product.size.toString())
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Filled.DateRange, contentDescription = null)
                    Text(text = "${product.created()} - ${product.expiration()}")
                }

                if (product.tags.isNotEmpty()) {
                    HorizontalDivider()
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        for (tag in product.tags.values) {
                            AssistChip(
                                onClick = { },
                                label = { Text(text = tag.name) },
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CustomBadgedBox(
    modifier: Modifier = Modifier,
    badge: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    var badgeHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .padding(top = badgeHeight / 2),
            content = content
        )
        Box(
            modifier = Modifier
                .padding(end = 16.dp)
                .align(Alignment.TopEnd)
                .onGloballyPositioned {
                    badgeHeight = with(density) {
                        it.size.height.toDp()
                    }
                },
            content = badge
        )
    }
}

@Preview
@Composable
fun ProductItemPreview() {
    LibraryTheme(dynamicColor = false) {
        ProductItem(
            product = Product(
                key = "{dd_123}",
                uid = "zowIOymNJtfvplky2x6nKsnT2592",
                name = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                timestamp = 0L,
                expirationTimestamp = 100000L,
                size = "0.2",
                state = State.CREATED,
                tags = mapOf("key" to TagName("key", "text")),
            )
        ) {
        }
    }
}

@Preview
@Composable
fun ProductItemWithoutTagsPreview() {
    LibraryTheme(dynamicColor = false) {
        ProductItem(
            product = Product(
                key = "{dd_123}",
                uid = "zowIOymNJtfvplky2x6nKsnT2592",
                name = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                timestamp = 0L,
                expirationTimestamp = 100000L,
                size = "0.2",
                state = State.CREATED
            )
        ) {
        }
    }
}

@Preview
@Composable
fun CustomBadgedBoxPreview() {
    LibraryTheme(dynamicColor = false) {
        CustomBadgedBox(
            badge = {
                Badge {
                    Text(text = "test")
                }
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .width(300.dp)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
            )
        }
    }
}