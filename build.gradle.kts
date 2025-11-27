import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "2.2.21"
	kotlin("kapt") version "2.2.21"
	id("io.quarkus") version "3.29.0"
}

group = "ch.judos.sentio"
version = "1.0"


repositories {
	mavenCentral()
	mavenLocal()
}


dependencies {
	implementation(enforcedPlatform(
		"io.quarkus.platform:quarkus-bom:3.30.1"))
	implementation("io.github.openfeign.querydsl:querydsl-jpa:7.1")
	implementation("io.quarkus:quarkus-hibernate-orm")
	implementation("io.quarkus:quarkus-agroal")
	implementation("io.quarkus:quarkus-kotlin")
	implementation("io.quarkus:quarkus-rest")
	implementation("io.quarkus:quarkus-rest-jackson")
	implementation("io.quarkus:quarkus-flyway")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.2.21")
	implementation("io.quarkus:quarkus-jdbc-mysql")
	implementation("io.quarkus:quarkus-jdbc-h2")
	
	implementation("io.quarkus:quarkus-qute")
	implementation("io.quarkus:quarkus-scheduler")
	
	implementation("org.sejda.imageio:webp-imageio:0.1.6")
	
	kapt("io.github.openfeign.querydsl:querydsl-apt:7.1:jpa")
	
	testImplementation("io.quarkus:quarkus-junit5")

	// JJWT for JWT creation and parsing
	implementation("io.jsonwebtoken:jjwt-api:0.13.0")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")
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
