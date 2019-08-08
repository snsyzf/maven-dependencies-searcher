import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.intellij") version "0.4.9"
    java
    kotlin("jvm") version "1.3.40"
}

group = "com.plugin"
version = "1.0.5"

repositories {
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    maven("https://maven.aliyun.com/repository/jcenter")
    maven("https://maven.aliyun.com/repository/spring")
    maven("https://maven.aliyun.com/repository/spring-plugin")
    maven("https://oss.sonatype.org/content/groups/public/")
    mavenCentral()
}

dependencies {

    
    testCompile("junit", "junit", "4.12")
    
    compile("com.squareup.okhttp3:okhttp:3.14.2")
    
    compile("com.github.axet:wget:1.4.9") {
        this.exclude(module = "jsoup")
        this.exclude(module = "commons-io")
        this.exclude(module = "xstream")
        this.exclude(module = "xpp3_min")
        this.exclude(module = "xmlpull")
    }
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2019.1.3"
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    
    version(project.version)
    sinceBuild("181.0")
    untilBuild("192.*")

}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
