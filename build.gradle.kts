import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "2.2.0"
	kotlin("kapt") version "2.2.0"
	id("io.quarkus") version "3.28.1"
}

group = "ch.judos.sentio"
version = "1.0"


val quarkusVersion by extra("3.25.4")
val kotlinVersion by extra("2.2.0")

repositories {
	mavenCentral()
	mavenLocal()
}


dependencies {
	implementation(enforcedPlatform(
		"io.quarkus.platform:quarkus-bom:3.25.4"))
	implementation("io.github.openfeign.querydsl:querydsl-jpa:7.0")
	implementation("io.quarkus:quarkus-hibernate-orm")
	implementation("io.quarkus:quarkus-agroal")
	implementation("io.quarkus:quarkus-kotlin")
	implementation("io.quarkus:quarkus-rest")
	implementation("io.quarkus:quarkus-rest-jackson")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
	implementation("io.quarkus:quarkus-jdbc-mysql")
	implementation("io.quarkus:quarkus-jdbc-h2")
	
	implementation("io.quarkus:quarkus-qute")
	implementation("io.quarkus:quarkus-scheduler")
	// testImplementation("io.quarkus:quarkus-junit5")
	// testImplementation("io.rest-assured:rest-assured")
	// testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
	
	// annotationProcessor("io.github.openfeign.querydsl:querydsl-apt:7.0:jpa")
	kapt("io.github.openfeign.querydsl:querydsl-apt:7.0:jpa")
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
	compilerOptions {
		jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
		javaParameters = true
	}
}

sourceSets {
	main {
		java {
			// srcDir("build/generated/sources/annotationProcessor/java/main")
			srcDir("build/generated/source/kapt/main")
		}
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

// val isNative = project.hasProperty("native")
// if (isNative) {
//     tasks.withType<Test> {
//         systemProperty("quarkus.package.type", "native")
//     }
// }

tasks.named("compileJava") {
    dependsOn("compileQuarkusGeneratedSourcesJava")
}
tasks.named("compileKotlin") {
    dependsOn("compileQuarkusGeneratedSourcesJava")
}
tasks.named("compileQuarkusGeneratedSourcesJava") {
    dependsOn("kaptGenerateStubsKotlin")
}
val compileKotlin: KotlinCompile by tasks

compileKotlin.compilerOptions {
	freeCompilerArgs.set(listOf("-Xannotation-default-target=param-property"))
}
