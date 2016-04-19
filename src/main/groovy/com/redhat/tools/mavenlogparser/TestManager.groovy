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

    Test getLastTest() {
        if (testList.size() > 0) {
            return testList.get(testList.size() - 1)
        } else {
            return null
        }
    }

    void addTest(testName, time) {
        def tname = getTestName(testName)
        Test test = new Test(name: tname, startTime: time)

        testList.add(test)
        testMap[test.name] = test
    }

    void addError(testName, err) {
        if (!hasTest(testName)) {
            addTest(testName)
        }
        getTest(testName).errors.add(err)
    }

    void addStop(testName, time) {
        if (hasTest(testName)) {
            Test test = getTest(testName)
            test.stop = true
            test.stopTime = time
        }
    }

    void addTimeForLastTest(time) {
        Test lastTest = getLastTest()
        if (lastTest != null) {
            if (lastTest.stopTime == null) {
                lastTest.stopTime = time
            }
        }
    }

    void addLineForLastTest(String line) {
        Test lastTest = getLastTest()
        if (lastTest != null) {
            lastTest.addLine(line)
        }
    }

    void closeLastTest() {
        Test lastTest = getLastTest()
        if (lastTest != null) {
            lastTest.close()
        }
    }

}
