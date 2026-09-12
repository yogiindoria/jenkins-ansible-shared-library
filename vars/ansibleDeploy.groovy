def call() {

    stage('Clone') {
        echo "Cloning AnsibleSonarQube repository..."

        git(
            url: 'https://github.com/yogiindoria/AnsibleSonarQube.git',
            branch: 'main',
            credentialsId: 'github-credentials'
        )

        echo "Repository cloned successfully."
    }
}