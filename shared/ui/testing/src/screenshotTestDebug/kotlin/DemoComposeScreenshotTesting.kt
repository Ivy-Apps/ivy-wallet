import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivy.ui.annotation.IvyPreviews

@Suppress("UnusedPrivateMember")
@IvyPreviews
@Composable
private fun DemoComposeScreenTesting() {
    Column {
        Spacer(Modifier.height(16.dp))
        Text(text = "Hello World")
        Spacer(Modifier.height(16.dp))
    }
}