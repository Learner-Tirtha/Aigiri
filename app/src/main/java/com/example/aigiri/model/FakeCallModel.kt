//package com.example.aigiri.model
//
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.ui.graphics.Color
//import java.util.*
//
//// Theme colors
//val primaryAigiri = Color(0xFF6F5A9D) // Rich purple
//val lightAigiri = Color(0xFFF5F5F5) // Light grayish background
//val darkAigiri = Color(0xFF6F5A9D) // Darker accent
//
//data class Preset(
//    val id: String = UUID.randomUUID().toString(),
//    val name: String,
//    val phone: String,
//    val color: Color = primaryAigiri, // Fixed to purple
//    val icon: androidx.compose.ui.graphics.vector.ImageVector,
//    val voiceType: VoiceType = VoiceType.MALE,
//    val relationship: relationshipType= relationshipType.Friend
//)
//
//enum class VoiceType {
//    MALE, FEMALE, CHILD
//}
//enum class relationshipType {
//    Father, Mother, Brother, Husband, Wife, Friend
//}
//enum class CallDuration(val label: String, val seconds: Int) {
//    FIVE_SEC("5 sec", 5),
//    TEN_SEC("10 sec", 10),
//    ONE_MIN("1 min", 60),
//    FIVE_MIN("5 min", 300)
//}
//
//data class ConversationLine(
//    val text: String,
//    val delayAfter: Long = 2000L
//)
//
//object ConversationData {
//    val conversations = mapOf(
//        "Father" to listOf(
//            ConversationLine("Hello! How are you doing?", 3000),
//            ConversationLine("I was just thinking about you.", 2500),
//            ConversationLine("How's work going?", 3000),
//            ConversationLine("Don't forget to call your mother.", 2800),
//            ConversationLine("Take care of yourself.", 2000)
//        ),
//        "Mother" to listOf(
//            ConversationLine("Hi sweetheart! How are you?", 3000),
//            ConversationLine("I made your favorite dish today.", 2800),
//            ConversationLine("Did you eat properly?", 2500),
//            ConversationLine("Don't work too hard, dear.", 3000),
//            ConversationLine("I love you so much!", 2000)
//        ),
//        "Brother" to listOf(
//            ConversationLine("Hey bro! What's up?", 2500),
//            ConversationLine("Did you watch the game last night?", 3000),
//            ConversationLine("We should hang out soon.", 2800),
//            ConversationLine("Mom asked about you.", 2500),
//            ConversationLine("Catch you later!", 2000)
//        ),
//        "Husband" to listOf(
//            ConversationLine("Hey honey! How's your day?", 3000),
//            ConversationLine("I'll pick up groceries on my way home.", 2800),
//            ConversationLine("What do you want for dinner?", 2500),
//            ConversationLine("I love you so much.", 2000),
//            ConversationLine("See you tonight!", 1800)
//        ),
//        "Wife" to listOf(
//            ConversationLine("Hi darling! Miss you!", 2800),
//            ConversationLine("The kids are asking about you.", 3000),
//            ConversationLine("Don't forget our anniversary.", 2500),
//            ConversationLine("I made your favorite tonight.", 2800),
//            ConversationLine("Love you too!", 2000)
//        ),
//        "Son" to listOf(
//            ConversationLine("Hi daddy! Guess what happened today!", 3000),
//            ConversationLine("I got an A on my test!", 2500),
//            ConversationLine("Can we play when you come home?", 2800),
//            ConversationLine("I drew you a picture!", 2500),
//            ConversationLine("Love you daddy!", 2000)
//        ),
//        "Friend" to listOf(
//            ConversationLine("Hey! What's up?", 2500),
//            ConversationLine("Are you free this weekend?", 2800),
//            ConversationLine("Let's grab coffee soon!", 2500),
//            ConversationLine("Talk to you later!", 2000)
//        )
//    )
//}
//
//object DefaultPresets {
//    val presets = listOf(
//        Preset(
//            name = "Dad",
//            phone = "1234567890",
//            color = primaryAigiri,
//            icon = Icons.Filled.Person,
//            voiceType = VoiceType.MALE,
//            relationship = relationshipType.Father
//        ),
//        Preset(
//            name = "Mom",
//            phone = "0987654321",
//            color = primaryAigiri,
//            icon = Icons.Filled.PersonOutline,
//            voiceType = VoiceType.FEMALE,
//            relationship = relationshipType.Mother
//        ),
//        Preset(
//            name = "Brother",
//            phone = "1122334455",
//            color = primaryAigiri,
//            icon = Icons.Filled.Face,
//            voiceType = VoiceType.MALE,
//            relationship = relationshipType.Brother
//        )
//    )
//
//    val availableIcons = listOf(
//        Icons.Filled.Person,
//        Icons.Filled.PersonOutline,
//        Icons.Filled.Face,
//        Icons.Filled.Man,
//        Icons.Filled.Woman,
//        Icons.Filled.FamilyRestroom
//    )
//}
//
//fun formatPhone(phone: String): String {
//    return if (phone.length == 10) {
//        "(${phone.substring(0, 3)}) ${phone.substring(3, 6)}-${phone.substring(6)}"
//    } else phone
//}