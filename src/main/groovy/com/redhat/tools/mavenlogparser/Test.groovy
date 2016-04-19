package com.redhat.tools.mavenlogparser

import java.text.SimpleDateFormat
import org.apache.commons.lang.time.DurationFormatUtils

class Test {

    String name

    def errors = []

    def stop = false

    def startTime = null

    def stopTime = null

    PrintWriter testLog = null

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
