plugins {
    `java`
}
group = "org.example"
dependencies {
    testImplementation(testFixtures("org.example:ComponentA"))
}

val a by tasks.registering(Task::class) {
    dependsOn("clean","assemble")
    doLast{
        var x = configurations["testCompileClasspath"].copyRecursive{it.group == "org.example" && it.name=="ComponentA"}
        resources.text.fromArchiveEntry(x,"tomcat.p12")
            .asFile().copyTo(file("build/testFolder/tomcat.p12"))
    }


}
