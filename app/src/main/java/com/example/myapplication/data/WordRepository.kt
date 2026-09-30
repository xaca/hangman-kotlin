package com.example.myapplication.data

object WordRepository {
    private val wordsByCategory = mapOf(
        "Animals" to listOf(
            "ELEPHANT", "GIRAFFE", "DOLPHIN", "KANGAROO", "PENGUIN",
            "CHEETAH", "LEOPARD", "OCTOPUS", "FLAMINGO", "CROCODILE",
            "SQUIRREL", "CHIMPANZEE", "RHINOCEROS", "HEDGEHOG", "BUTTERFLY"
        ),
        "Movies & Shows" to listOf(
            "AVATAR", "INCEPTION", "TITANIC", "GLADIATOR", "INTERSTELLAR",
            "MATRIX", "CASABLANCA", "JUMANJI", "STARWARS", "BREAKINGBAD",
            "GODFATHER", "INCREDIBLES", "SPIDERMAN", "STRANGERTHINGS"
        ),
        "Sports" to listOf(
            "FOOTBALL", "BASKETBALL", "BASEBALL", "TENNIS", "VOLLEYBALL",
            "CRICKET", "SWIMMING", "ATHLETICS", "GYMNASTICS", "SNOWBOARDING",
            "BADMINTON", "SKATEBOARDING", "TAEKWONDO", "WATERPOLO"
        ),
        "Countries" to listOf(
            "BRAZIL", "JAPAN", "CANADA", "GERMANY", "AUSTRALIA",
            "ARGENTINA", "MEXICO", "FRANCE", "ITALY", "EGYPT",
            "PORTUGAL", "SWITZERLAND", "MADAGASCAR", "NETHERLANDS"
        ),
        "Technology" to listOf(
            "ANDROID", "KOTLIN", "COMPUTER", "SOFTWARE", "INTERNET",
            "DATABASE", "ALGORITHM", "BLUETOOTH", "HARDWARE", "PROCESSOR",
            "MICROPROCESSOR", "SMARTPHONE", "CYBERSECURITY", "ENCRYPTION"
        ),
        "Food & Drinks" to listOf(
            "PIZZA", "HAMBURGER", "SPAGHETTI", "CHOCOLATE", "PANCAKES",
            "CAPPUCCINO", "GUACAMOLE", "SANDWICH", "CROISSANT", "MILKSHAKE",
            "LASAGNA", "BURRITO", "CHEESECAKE", "SMOOTHIE"
        )
    )

    fun getRandomWord(category: String): String {
        val list = wordsByCategory[category] ?: wordsByCategory.values.flatten()
        return list.random()
    }
}
