package com.example.faunafinder.ui.feed

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.SpanStyle
import com.example.faunafinder.R
import androidx.compose.ui.text.withStyle


@Composable
fun FeedScreen() {
    val context = LocalContext.current
    val latitude = 4.623978 //
    val longitude = -74.064417 //
    val locationName = "Parque Nacional, Colombia"



    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            elevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.animal),
                    contentDescription = "Foto del animal",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Texto clickeable que abre Google Maps
                val locationText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                        append("📍 Ubicación: $locationName")
                    }
                }

                Text(
                    text = locationText,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.clickable {
                        val uri = "geo:$latitude,$longitude?q=$locationName"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        intent.setPackage("com.google.android.apps.maps") // Asegura que se abra en Google Maps
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "¡Mira este animal que encontré!", style = MaterialTheme.typography.body1)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFeedScreen() {
    FeedScreen()
}
