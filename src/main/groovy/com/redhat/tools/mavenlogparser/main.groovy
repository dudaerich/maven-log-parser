package com.redhat.tools.mavenlogparser

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

def cli = new CliBuilder(usage: 'maven-log-parser [options] logfile')
cli.errlines(args:1, argName:'number', 'number of error lines which will be printed for each test')
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

def errlines = options.getProperty('errlines')?:3
def knownissues = options.getProperty('knownissues') ?
        new FileInputStream(options.getProperty('knownissues')).readLines().toSet()
        : new HashSet<String>()
def logfile = new FileInputStream(options.arguments()[0])

def testsResult = Parser.parse(logfile)

AnsiConsole.systemInstall();

testsResult.each {
    print ansi().fg(CYAN).a("TEST ").reset()

    if (it.errors.size() && knownissues.contains(it.name)) {
        println ansi().fg(YELLOW).a("${it.name} <<< KNOWN ISSUE").reset()
    } else if (it.errors.size()) {
        println ansi().fg(MAGENTA).a("${it.name} <<< ERROR").reset()
    } else {
        println ansi().fg(GREEN).a("${it.name} <<< SUCCESS").reset()
    }
    it.errors.each {
        it.take(errlines).each { println ansi().fg(RED).a(it).reset()}
        println ""
    }
}