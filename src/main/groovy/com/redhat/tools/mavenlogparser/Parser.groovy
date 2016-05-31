package com.redhat.tools.mavenlogparser

class Parser {

    def static error = /^(.+)\((.+)\).+<<< (ERROR!|FAILURE!)$/
    def static emptyLine = /^\s*$/
    def static time = /(\d+:\d+:\d+,\d+) /

    static List<Test> parse(InputStream is) {

        def testManager = new TestManager()

        def testError = []
        def testErrorName = null
        def processingTestError = false
        def lastTime = null
        is.eachLine {line ->
            def startClass = isStartClass(line)
            def startTest = isStartTest(line)
            def stopTest = isStopTest(line)
            def isError = line =~ error
            def isEmptyLine = line =~ emptyLine
            def isTime = line =~ time

            if (isTime) {
                lastTime = isTime[0][1]
            }

            if (startClass != false) {
                testManager.setClass(startClass[0][1])
            } else if (startTest != false) {
                testManager.addTimeForLastTest(lastTime)
                testManager.closeLastTest()
                testManager.addTest(startTest[0][1], lastTime)
            } else if (stopTest != false) {
                testManager.addStop(stopTest[0][1], lastTime)
            } else if (isError) {
                processingTestError = true
                testError = []
                testErrorName = "${isError[0][2]}.${isError[0][1]}"
            } else if (processingTestError && isEmptyLine) {
                testManager.addError(testErrorName, testError, lastTime)
                processingTestError = false
            } else if (processingTestError) {
                testError.add(line)
            }
            testManager.addLineForLastTest(line)
        }
        testManager.addTimeForLastTest(lastTime)
        testManager.closeLastTest()

        return testManager.testList
    }

    static Object isStartTest(String line) {
        def tests = [
            /^.*\s+Start test\s+-+\s+(.+)$/,
            /^.*#\*#\*# Starting test: (.+)(\[.*\])?\(\)\.\.\.$/
        ]

        for (def test : tests) {
            def result = line =~ test
            if (result) {
                return result
            }
        }
        return false
    }

    static Object isStartClass(String line) {
        def tests = [
                /^Running (.+)$/
        ]

        for (def test : tests) {
            def result = line =~ test
            if (result) {
                return result
            }
        }
        return false
    }

    static Object isStopTest(String line) {
        def tests = [
            /^.*\s+Stop test\s+-+\s+(.+)$/,
            /^.*#\*#\*# Finished test: (.+)(\[.*\])?\(\)\.\.\.$/
        ]

        for (def test : tests) {
            def result = line =~ test
            if (result) {
                return result
            }
        }
        return false
    }

}