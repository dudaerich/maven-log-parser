package com.redhat.tools.mavenlogparser

/**
 * Created by eduda on 3.9.2015.
 */
class TestManager {

    def testList = []
    def testMap = [:]

    def addTest(Test test) {
        testList.add(test)
        testMap[test.name] = test
    }

    def addError(testName, err) {
        if (testMap[testName]) {
            testMap[testName].errors.add(err)
        }
    }

}
