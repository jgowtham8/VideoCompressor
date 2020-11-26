package com.epicapps.videocompress_test

fun Int.toFileDuration(): String {
    val positionInMilliSecond = this
    var mins = 0
    var seconds = 0
    var miniSeconds = positionInMilliSecond/10

    if (miniSeconds >= 100){
        seconds = (miniSeconds / 100)
        miniSeconds %= 100
    }
    if (seconds >= 60){
        mins = (seconds / 60)
        seconds %= 60
    }

    var minStr = mins.toString()
    var secStr = seconds.toString()
    var miniSecStr = miniSeconds.toString()
    if (minStr.length == 1){
        minStr = "0$minStr"
    }
    if (secStr.length == 1){
        secStr = "0$secStr"
    }
    if (miniSecStr.length == 1){
        miniSecStr = "0$miniSecStr"
    }

    return "$minStr : $secStr : $miniSecStr" //when changing this format, change R.string.timerReset also
}