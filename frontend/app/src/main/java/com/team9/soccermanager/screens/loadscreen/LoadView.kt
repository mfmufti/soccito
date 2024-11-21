package com.team9.soccermanager.screens.loadscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team9.soccermanager.R

@Composable
fun LoadView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(40.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.soccer_load),
                    contentDescription = "Rounded Image",
                    modifier = Modifier.fillMaxSize(), // Ensures image fills the Box
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "Soccito",
                fontSize = 32.sp, // Large font size
                fontWeight = FontWeight.Bold, // Bold text
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(bottom = 27.dp)
            )

            CircularProgressIndicator(
                modifier = Modifier.width(50.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}