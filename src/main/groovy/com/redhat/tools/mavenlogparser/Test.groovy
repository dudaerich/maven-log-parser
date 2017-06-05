package com.redhat.tools.mavenlogparser

import java.text.SimpleDateFormat
import org.apache.commons.lang.time.DurationFormatUtils

class Test {

    String name

    def errors = []

    def stop = false

    def startTime = null

    def stopTime = null

    def duration = null

    PrintWriter testLog = null

    String getDurationString() {
        try {
            return DurationFormatUtils.formatDuration(getDuration(), 'HH:mm:ss')
        } catch (Exception e) {
            e.printStackTrace()
            return "???"
        }
    }

    long getDuration() {
        if (duration == null) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss,SSS")

            if (startTime != null && stopTime != null) {

                Date startDate = formatter.parse(startTime)
                Date stopDate = formatter.parse(stopTime)
                duration = stopDate.getTime() - startDate.getTime()

            } else {
                duration = 0
            }
        }
        return duration
    }

    void addLine(String line) {
        if (!Settings.extractTestLogs) {
            return
        }
        openTestLog()
        testLog.println(line)
    }

    void close() {
        if (testLog != null) {
            testLog.close()
        }
    }

    private void openTestLog() {
        if (testLog != null) {
            return
        }
        File dir = new File(Settings.testLogsDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        File testLogFile = new File(dir, name)
        testLog = new PrintWriter(new FileOutputStream(testLogFile, false))
    }

}
