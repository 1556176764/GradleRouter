package com.github.gradlerouter.moudle_reading

import android.app.Activity
import com.github.gradlerouter.annotations.Destination

@Destination(
    url = "router://reading",
    description = "阅读页"
)
class ReadingActivity : Activity() {

}