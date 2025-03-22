plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.1"
}

group = "fun.jaobabus"
version = "1.2.A-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    mavenLocal() // Используем локальный Maven-репозиторий
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds") // Отключаем кеширование версий
}

dependencies {
    // implementation("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")
    implementation("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-api:4.14.0")
    implementation("net.kyori:examination-api:1.3.0")

    implementation("fun.jaobabus:commandlib:0.2.1-SNAPSHOT")

    // Дополнительные зависимости
    implementation("com.sk89q.worldguard:worldguard-bukkit:7.0.9") {
        exclude (
            group = "org.spigotmc",
            module = "spigot-api"
        )
    }

    implementation("net.md-5:bungeecord-api:1.21-R0.1-SNAPSHOT")

    implementation("org.yaml:snakeyaml:2.4")

    implementation("com.google.guava:guava:32.1.2-jre")
    implementation("com.google.code.gson:gson:2.7")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

}

var mainClass = "fun.jaobabus.stafftolls.main.Main";

// Шейдинг CommandLib внутрь плагина
var shadowJarCli = tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJarCli") {
    archiveClassifier.set("cli")
    configurations = listOf(project.configurations.runtimeClasspath.get())

    dependencies {
        include(dependency("fun.jaobabus:commandlib:0.2.1-SNAPSHOT"))
        include(dependency("net.md-5:bungeecord-api:1.21-R0.1-SNAPSHOT"))
        include(dependency("com.sk89q.worldguard:worldguard-bukkit:7.0.9"))
        include(dependency("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT"))
        // include(dependency("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT"))
        include(dependency("com.google.guava:guava:32.1.2-jre"))
        include(dependency("org.yaml:snakeyaml:2.4"))
        include(dependency("net.kyori:adventure-api"))
        include(dependency("com.google.code.gson:gson:2.7"))
        include(dependency("net.kyori:examination-api:1.3.0"))
    }

    from(project.configurations.runtimeClasspath.get().filter {
        it.name.contains("guava") || it.name.contains("gson")
    })

    from(sourceSets.main.get().output)

    // relocate("org.bukkit", "fun.jaobabus.libs.bukkit")
    // relocate("com.google.common", "fun.jaobabus.libs.guava")

    manifest {
        attributes(
            "Main-Class" to mainClass
        )
    }
}

var configurePlugin = tasks.register<JavaExec>("configurePlugin") {
    dependsOn(shadowJarCli)

    mainClass.set("-jar")

    args = listOf(
        "./build/libs/StaffTolls-$version-cli.jar",
        "chain",
        "yaml ./src/main/resources/plugin.yml",
        "latest -p:plugin -p:cli ./build/libs/StaffTolls-$version-{1}.jar ./build/libs/StaffTolls-latest-{1}.jar"
    )

    isIgnoreExitValue = false
}

var shadowJarPlugin = tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJarPlugin") {
    dependsOn(configurePlugin)

    archiveClassifier.set("plugin") // plugin.jar
    configurations = listOf(project.configurations.runtimeClasspath.get())

    dependencies {
        include(dependency("fun.jaobabus:commandlib:0.2.1-SNAPSHOT"))
    }

    from(sourceSets.main.get().output)
}

var latestPlugin = tasks.register<JavaExec>("latestPlugin") {
    dependsOn(shadowJarCli)

    mainClass.set("-jar")

    args = listOf(
        "./build/libs/StaffTolls-$version-cli.jar",
        "chain",
        "latest -p:plugin -p:cli ./build/libs/StaffTolls-$version-{1}.jar ./build/libs/StaffTolls-latest-{1}.jar"
    )

    isIgnoreExitValue = false
}

tasks.build {
    dependsOn(shadowJarPlugin, latestPlugin)
}

publishing {
    publications {
        val verIncrement = 0;
        create<MavenPublication>("mavenJava") {
            groupId = "fun.jaobabus"
            artifactId = "stafftools"
            version = "1.2.$verIncrement-SNAPSHOT"
            from(components["java"])
        }
    }
}
