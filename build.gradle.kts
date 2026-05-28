plugins {
    id("java")
    // Este plugin gestiona Lombok de forma automática y mucho más estable
    id("io.freefair.lombok") version "8.6"
    // Plugin de aplicación para definir la clase principal
    id("application")
}

group = "com.tp.jpa"
version = "1.0-SNAPSHOT"

// Define la clase principal para gradle run
application {
    mainClass.set("com.tp.jpa.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

repositories {
    mavenCentral()
}

dependencies {
    // JUnit para pruebas
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Hibernate (Implementación de JPA)
    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")

    // Driver de H2 Database
    runtimeOnly("com.h2database:h2:2.2.224")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf(
        "--add-opens", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
    ))
}