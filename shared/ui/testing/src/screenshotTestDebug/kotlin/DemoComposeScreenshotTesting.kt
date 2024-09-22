import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivy.ui.annotation.IvyPreviews

@Suppress("UnusedPrivateMember")
@IvyPreviews
@Composable
private fun DemoComposeScreenTesting() {
    Column {
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)
                .background(Color.Cyan)

        ) {
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
                    .background(Color.Red)

            )
        }
        Spacer(Modifier.height(16.dp))
    }
}