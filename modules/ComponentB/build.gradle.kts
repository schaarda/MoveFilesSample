plugins {
    `java`
}
group = "org.example"

// eigenes Attribut definieren
val etcExtracted = Attribute.of("etcExtracted", Boolean::class.javaObjectType)
// eigene Configuration damit es nur die gewünschte Dep trifft
val etcConfiguration by configurations.creating {
    isTransitive = false
    // etcExtracted=true wird benötigt
    attributes.attribute(etcExtracted,true)
}

configurations {
    testImplementation {
        extendsFrom(etcConfiguration)   // so muss ich die Dep die auch in etcconfig ist nicht 2* definieren
        isTransitive = true
    }
}

configurations.all {
    afterEvaluate {
        if (isCanBeResolved) {
            resolve()        // trigger resolve to test
        }
    }
}

// Transformer zum Entpacken
abstract class Unzip : TransformAction<TransformParameters.None> {
    @get:InputArtifact
    abstract val inputArtifact: Provider<FileSystemLocation>

    override
    fun transform(outputs: TransformOutputs) {
        val input = inputArtifact.get().asFile
        val unzipDir = outputs.dir(input.name)
        unzipTo(input, unzipDir)
    }

    private fun unzipTo(zipFile: File, unzipDir: File) {
        println("unzip $zipFile to $unzipDir")
        println("File exists? ${zipFile.exists()}")
    }
}



dependencies {
    // eigenes Attribut registrieren
    attributesSchema {
        attribute(etcExtracted)
    }
    // Transformer registrieren
    registerTransform(Unzip::class) {
        from.attribute(etcExtracted,false)
        to.attribute(etcExtracted,true)
    }

    // und jede jar hat das Attribut per default auf false
    artifactTypes.getByName("jar") {
        attributes.attribute(etcExtracted, false)
    }
    // zu entpackende Dep
    etcConfiguration(testFixtures("org.example:ComponentA"))
}



val a by tasks.registering(Task::class) {
    dependsOn("clean","assemble")
    doLast{
        println("hi")
    }





}
