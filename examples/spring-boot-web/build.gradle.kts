import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("org.springframework.boot") version "3.0.1"
    id("io.spring.dependency-management") version "1.1.0"
//	id("org.graalvm.buildtools.native") version "0.9.18"
}

group = "top.mrys.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenLocal()
    //	本地仓库
    maven {
        url = uri("file:///D:/WorkSpace/.m2/repository")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/public/")
    }
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    //swagger
    testImplementation("io.springfox:springfox-boot-starter:3.0.0")
    //api doc
    implementation("top.mrys:mrys-api-doc:1.0-SNAPSHOT")
    implementation("top.mrys:mrys-security:1.0-SNAPSHOT")
    implementation("top.mrys:mrys-mybatis-plus:1.0-SNAPSHOT")
    implementation("top.mrys:mrys-web:1.0-SNAPSHOT")
    implementation("top.mrys:mrys-valid:1.0-SNAPSHOT")
    implementation("cn.hutool:hutool-crypto:5.8.11")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<BootJar> {
    archiveFileName.set("boot.jar")
}