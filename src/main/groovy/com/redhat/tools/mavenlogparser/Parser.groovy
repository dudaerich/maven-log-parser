package com.redhat.tools.mavenlogparser

class Parser {

    def static startTest = /^.*\s+Start test\s+-+\s+(.+)$/
    def static error = /^(.+)\((.+)\).+<<< ERROR!$/
    def static emptyLine = /^\s*$/

    static List<Test> parse(InputStream is) {

        def testManager = new TestManager()

        def testError = []
        def testErrorName = null
        def processingTestError = false
        is.eachLine {line ->
            def isStartTest = line =~ startTest
            def isError = line =~ error
            def isEmptyLine = line =~ emptyLine
            if (isStartTest) {
                testManager.addTest(new Test(name: isStartTest[0][1]))
            } else if (isError) {
                processingTestError = true
                testError = []
                testErrorName = "${isError[0][2]}.${isError[0][1]}"
            } else if (processingTestError && isEmptyLine) {
                testManager.addError(testErrorName, testError)
                processingTestError = false
            } else if (processingTestError) {
                testError.add(line)
            }
        }

        return testManager.testList
    }

}