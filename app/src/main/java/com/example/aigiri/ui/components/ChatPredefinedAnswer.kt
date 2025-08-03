package com.example.aigiri.ui.components


object ChatPredefinedAnswers {
    val answers = mapOf(
        "What should I do if I feel unsafe?" to "If you feel unsafe, immediately contact your family, nearby police, or use a women safety app to share your location by clicking on SOS in navigation.",
        "How can I report harassment?" to "You can report harassment by contacting the women's helpline 1091 or using online portals like cybercrime.gov.in.",
        "Is there any emergency number?" to "Yes, the national women helpline number in India is 1091, and the general emergency number is 112.",
        "What should I carry for self defense?" to "You can carry pepper spray, personal alarms, or even learn basic self-defense techniques."
    )

    val questions = answers.keys.toList()
}
