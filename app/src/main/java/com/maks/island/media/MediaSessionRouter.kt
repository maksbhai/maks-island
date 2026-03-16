package com.maks.island.media

import com.maks.island.domain.models.MediaState

class MediaSessionRouter {
    fun mockNowPlaying(): MediaState = MediaState("Aurora", "Maks", true, 0.2f)
}
