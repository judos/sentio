# Native build

	./gradlew build -D"quarkus.native.enabled"=true -D"quarkus.native.container-build"=true -D"quarkus.package.jar.enabled"=false

run it:

	./sentio-1.0-runner -Dquarkus.config.locations=application.properties

