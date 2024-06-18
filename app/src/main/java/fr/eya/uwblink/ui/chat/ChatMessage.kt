 package fr.eya.uwblink.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.eya.uwblink.BluetoothChat.domain.BluetoothMessage
import fr.eya.uwblink.ui.theme.MyAppTheme
import fr.eya.uwblink.ui.theme.OldRose
import fr.eya.uwblink.ui.theme.Vanilla


 @Composable
 fun UserIcon(
     modifier: Modifier = Modifier
 ) {
     Icon(
         imageVector = Icons.Default.Person,
         contentDescription = "User Icon",
         modifier = modifier
             .padding(end = 8.dp)
             .background(color = Color.Blue, shape = CircleShape)
     )
 }

 @Composable
 fun ChatMessage(
     modifier: Modifier = Modifier,
     message: BluetoothMessage,
 ) {
     Row(
         modifier = modifier
             .clip(
                 RoundedCornerShape(
                     topStart = if (message.isFromLocalUser) 15.dp else 0.dp,
                     topEnd = 15.dp,
                     bottomStart = 15.dp,
                     bottomEnd = if (message.isFromLocalUser) 0.dp else 15.dp
                 )
             )
             .background(
                 if (message.isFromLocalUser) OldRose else Vanilla
             )
             .padding(16.dp)
     ) {

         Column {
             UserIcon()
             Text(
                 text = message.senderName + ":",
                 fontSize = 10.sp,
                 color = Color.Black
             )
             Text(
                 text = message.message,
                 fontSize = 10.sp,
                 color = Color.Black,
                 modifier = Modifier.widthIn(max = 250.dp)
             )
         }
     }
 }

 @Preview
 @Composable
 fun ChatMessagePreview() {
     MyAppTheme {
         ChatMessage(
             message = BluetoothMessage(
                 message = "Hello World!",
                 senderName = "Pixel 6",
                 isFromLocalUser = false
             )
         )
     }
 }