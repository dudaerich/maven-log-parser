package com.redhat.tools.mavenlogparser

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

def cli = new CliBuilder(usage: 'maven-log-parser [options] logfile')
cli.errlines(args:1, argName:'number', 'number of error lines which will be printed for each test')
cli.showsuccess(args:0, 'flag which defined whether you want to see success tests')
cli.knownissues(args:1, argName:'file', 'file, which contains list of known failed tests. Each test is described by its fully Class name. For example: package.Test.testMethod')
cli.help('print this help')
def options = cli.parse(args)

if (options.getProperty('help')) {
    cli.usage()
    System.exit(0)
}

if (!options.arguments().size()) {
    println "Must specify path to the log file."
    cli.usage()
    System.exit(1)
}

def errlines = options.getProperty('errlines')?options.getProperty('errlines').toInteger():3
def showsuccess = options.getProperty('showsuccess')?true:false
def knownissues = options.getProperty('knownissues') ?
        new FileInputStream(options.getProperty('knownissues')).readLines().toSet()
        : new HashSet<String>()
def logfile = new FileInputStream(options.arguments()[0])

def testsResult = Parser.parse(logfile)

AnsiConsole.systemInstall();

testsResult.each {
    if (it.errors.size() && knownissues.contains(it.name)) {
        print ansi().fg(CYAN).a("TEST ").reset()
        println ansi().fg(YELLOW).a("${it.name} <<< KNOWN ISSUE").reset()
    } else if (it.errors.size()) {
        print ansi().fg(CYAN).a("TEST ").reset()
        println ansi().fg(MAGENTA).a("${it.name} <<< ERROR").reset()
    } else if (!it.errors.size() && !it.stop) {
        print ansi().fg(CYAN).a("TEST ").reset()
        println ansi().fg(MAGENTA).a("${it.name} <<< TIMEDOUT").reset()
    } else if (showsuccess) {
        print ansi().fg(CYAN).a("TEST ").reset()
        println ansi().fg(GREEN).a("${it.name} <<< SUCCESS").reset()
    }
    it.errors.each {
        it.take(errlines).each { println ansi().fg(RED).a(it).reset()}
        println ""
    }
}