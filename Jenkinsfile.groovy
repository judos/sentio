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

	docker.image('gradle:8.14-jdk21').inside(
			"-v $HOME/.gradle:/root/.gradle " +
			"-v /var/run/docker.sock:/var/run/docker.sock " +
			"-v /usr/bin/docker:/usr/bin/docker " +
			"--name sentio-build"
	) {
		sh ''' 
			apt-get update 
			apt-get install -y docker.io
		'''
		stage('Native build') {
			sh "gradle -Pversion=${version} build"
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
