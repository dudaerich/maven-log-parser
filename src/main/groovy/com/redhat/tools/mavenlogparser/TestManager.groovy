package com.redhat.tools.mavenlogparser

/**
 * Created by eduda on 3.9.2015.
 */
class TestManager {

    def className = null
    def testList = []
    def testMap = [:]

    void setClass(className) {
        this.className = className
    }

    String getTestName(String testName) {
        if (className == null) {
            return testName
        } else {
            if (testName.startsWith(className)) {
                return testName
            } else {
                return "${className}.${testName}"
            }
        }
    }

    boolean hasTest(String testName) {
        def tname = getTestName(testName)
        return testMap.containsKey(tname)
    }

    Test getTest(String testName) {
        def tname = getTestName(testName)
        return testMap[tname]
    }

    def addTest(testName) {
        def tname = getTestName(testName)
        Test test = new Test(name: tname)
        testList.add(test)
        testMap[test.name] = test
    }

    def addError(testName, err) {
        if (!hasTest(testName)) {
            addTest(testName)
        }
        getTest(testName).errors.add(err)
    }

    def addStop(testName) {
        if (hasTest(testName)) {
            getTest(testName).stop = true
        }
    }

}
