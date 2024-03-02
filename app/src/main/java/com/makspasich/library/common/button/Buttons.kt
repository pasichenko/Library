package com.makspasich.library.common.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.makspasich.googlebutton.GoogleButton
import com.makspasich.googlebutton.dark.round.Continue
import com.makspasich.googlebutton.light.round.Continue
import com.makspasich.library.ui.theme.GoogleButtonDarkBorder
import com.makspasich.library.ui.theme.GoogleButtonLightBorder

@Composable
fun SignOutButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(CircleShape)
            .border(1.dp, buttonColor().borderColor, CircleShape)
            .background(buttonColor().containerColor)
            .clickable(onClick = onClick),
        Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            style = MaterialTheme.typography.labelLarge.copy(color = buttonColor().contentColor),
            text = "Sign out"
        )
    }
}

@Composable
private fun buttonColor() = if (isSystemInDarkTheme()) lightButtonColor() else darkButtonColor()

@Composable
private fun lightButtonColor() = ButtonColors(
    containerColor = Color.White,
    contentColor = Color.Black,
    borderColor = GoogleButtonLightBorder,
)

@Composable
private fun darkButtonColor() = ButtonColors(
    containerColor = Color.Black,
    contentColor = Color.White,
    borderColor = GoogleButtonDarkBorder,
)

@Immutable
class ButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val borderColor: Color,
)


@Composable
fun GoogleLoginButton(
    onClick: () -> Unit
) {
    Image(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick),
        imageVector = if (isSystemInDarkTheme()) GoogleButton.Light.Round.Continue else GoogleButton.Dark.Round.Continue,
        contentDescription = "Google login"
    )
}

@Preview(showBackground = false)
@Composable
fun IconTest() {
    MaterialTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Image(
                imageVector = GoogleButton.Light.Round.Continue,
                contentDescription = "Google logo",
            )
            Image(
                imageVector = GoogleButton.Dark.Round.Continue,
                contentDescription = "Google logo",
            )
        }
    }
}
