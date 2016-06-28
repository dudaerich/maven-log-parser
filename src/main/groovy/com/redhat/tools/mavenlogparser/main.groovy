package com.redhat.tools.mavenlogparser

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

def cli = new CliBuilder(usage: 'maven-log-parser [options] logfile')
cli.e(longOpt: 'errlines', args:1, argName:'number', 'number of error lines which will be printed for each test')
cli.s(longOpt: 'showsuccess', args:0, 'flag which defines whether you want to see success tests')
cli.k(longOpt: 'knownissues', args:1, argName:'file', 'file, which contains list of known failed tests. Each test is described by its fully Class name. For example: package.Test.testMethod')
cli.x(longOpt: 'extract', args:0, 'flag which defines whether you want to extract test logs or not')
cli.S(longOpt: 'sort', args:1, argName:'field', 'sort tests by field. Possible values: name, duration')
cli.R(longOpt: 'reverse', args:0, 'it reverses sequence of tests. It is useful with --sort option.')
cli.h(longOpt: 'help', 'print this help')
def options = cli.parse(args)

if (options.h) {
    cli.usage()
    System.exit(0)
}

if (options.arguments().size() == 0) {
    println "Must specify path to the log file."
    cli.usage()
    System.exit(1)
}

if (options.getProperty('extract')) {
    Settings.extractTestLogs = true
}

def errlines = options.getProperty('errlines')?options.getProperty('errlines').toInteger():3
def showsuccess = options.getProperty('showsuccess')?true:false
def knownissues = options.getProperty('knownissues') ?
        new FileInputStream(options.getProperty('knownissues')).readLines().toSet()
        : new HashSet<String>()
def logfile = new FileInputStream(options.arguments()[0])

def testsResult = Parser.parse(logfile)

AnsiConsole.systemInstall();

if (options.getProperty('sort')) {
    switch (options.getProperty('sort')) {
        case 'name': testsResult.sort { it.getName() }; break
        case 'duration': testsResult.sort { it.getDuration() }; break
        default:
            println "Wrong sort option"
            cli.usage()
            System.exit(1)
    }
}

if (options.getProperty('reverse')) {
    testsResult.reverse(true)
}

testsResult.each {

    if (it.errors.size() && knownissues.contains(it.name)) {
        print ansi().fg(CYAN).a("TEST ").reset()
        println ansi().fg(YELLOW).a("${it.name} <<< KNOWN ISSUE (${it.getDurationString()})").reset()
    } else if (it.errors.size()) {
        print ansi().fg(CYAN).a("TEST ").reset()
        println ansi().fg(MAGENTA).a("${it.name} <<< ERROR (${it.getDurationString()})").reset()
    } else if (!it.errors.size() && !it.stop) {
        print ansi().fg(CYAN).a("TEST ").reset()
        println ansi().fg(MAGENTA).a("${it.name} <<< TIMEDOUT (${it.getDurationString()})").reset()
    } else if (showsuccess) {
        print ansi().fg(CYAN).a("TEST ").reset()
        println ansi().fg(GREEN).a("${it.name} <<< SUCCESS (${it.getDurationString()})").reset()
    }
    it.errors.each {
        it.take(errlines).each { println ansi().fg(RED).a(it).reset()}
        println ""
    }
}