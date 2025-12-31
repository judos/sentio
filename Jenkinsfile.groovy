node {

	stage('Checkout') {
		echo "$HOME"
		checkout scm
	}

	def version = ""
	stage('Version') {
		sh 'git fetch --prune-tags'
		sh 'chmod +x ./nv.sh'
		version = sh(script: './nv.sh', returnStdout: true).trim()
		echo "Building version: ${version}"
	}

	docker.image('quay.io/quarkus/ubi-quarkus-mandrel-builder-image:23.1-jdk-21').inside(
			"-v $HOME/.gradle:/root/.gradle " +
			"--user root:root " +
			"--name sentio-build " +
			"--entrypoint /bin/bash "
	) {
		stage('Native build') {
			sh 'chmod +x gradlew'
			sh "./gradlew -Pversion=${version} build"
			sh 'cp build/sentio-native-runner docker/sentio-native'
		}
	}

	stage('Deploy') {
		dir('docker') {
			sh "version=${version} docker-compose -f docker-compose.yml build"
			sh "version=${version} docker-compose -f docker-compose.yml stop"
			sh "version=${version} docker-compose -f docker-compose.yml up -d"
		}

		// jenkins has own github deploy key which can be used for this repo only
		// also this ssh key is configured to be used for this repo in ~/.ssh/config
		sh "git tag ${version}"
		sh "git push --tags"
	}
}
