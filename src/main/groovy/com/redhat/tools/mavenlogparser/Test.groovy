package com.redhat.tools.mavenlogparser

import java.text.SimpleDateFormat
import org.apache.commons.lang.time.DurationFormatUtils

class Test {

    def name

    def errors = []

    def stop = false

    def startTime = null

    def stopTime = null

    String getDuration() {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss,SSS")
            Date startDate = formatter.parse(startTime)
            Date stopDate = formatter.parse(stopTime)
            long diff = stopDate.getTime() - startDate.getTime()
            return DurationFormatUtils.formatDuration(diff, 'HH:mm:ss')
        } catch (Exception e) {
            e.printStackTrace()
            return "???"
        }
    }

}
