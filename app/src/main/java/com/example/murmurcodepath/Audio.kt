package com.example.murmurcodepath

data class Audio(
    val id: Int = 0,
    val parentUrl: String,
    val artistName: String? = null,
    val artistUrl: String? = null,
    val title: String? = null,
    val playableAudioLink: String? = null,
    val description: String? = null,
    val tags: Set<String> = emptySet()
) {
    fun isEmpty() = artistName == null &&
        artistUrl == null &&
        title == null &&
        playableAudioLink == null &&
        description == null &&
        tags.isEmpty()

    companion object {
        @JvmStatic
        fun getTestAudio(): Audio {
            return Audio(
                artistName = "JustAnotherVoiceInTheCloud",
                title = "The Strange Case of Dr. Jekyll and Mr. Hyde",
                description = "Dr Jekyll and Mr Hyde is about the complexities of science and the" +
                    " duplicity of human nature. Dr Jekyll is a well-respected and intelligent scientist who meddles with the darker side of science.",
                tags = setOf(
                    "Offbeat",
                    "Motivational",
                    "Breaking the fourth wall",
                    "Sound effects",
                    "Singing",
                    "Comfort",
                    "Collaboration",
                    "Singing",
                    "Bedtime lullaby"
                ),
                parentUrl = "",
                playableAudioLink = "asset:///sacrifice.mp3"
            )
        }
    }
}