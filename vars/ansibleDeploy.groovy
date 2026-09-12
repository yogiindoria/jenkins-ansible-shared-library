def call() {

    // Load configuration file
    def configText = libraryResource('sonarqube.properties')
    def config = new Properties()

    config.load(new StringReader(configText))

    def slackChannel = config.getProperty('SLACK_CHANNEL_NAME')
    def environment = config.getProperty('ENVIRONMENT')
    def codeBasePath = config.getProperty('CODE_BASE_PATH')
    def actionMessage = config.getProperty('ACTION_MESSAGE')
    def keepApproval = config.getProperty('KEEP_APPROVAL_STAGE').toBoolean()

    stage('Clone') {

        echo "Cloning AnsibleSonarQube repository..."

        git(
            url: 'https://github.com/yogiindoria/AnsibleSonarQube.git',
            branch: 'main',
            credentialsId: 'github-credentials'
        )

        echo "Repository cloned successfully."
    }

    stage('User Approval') {

        if (keepApproval) {

            input(
                message: "Do you want to deploy SonarQube to ${environment}?",
                ok: 'Proceed'
            )

        } else {

            echo "Approval stage skipped."
        }
    }

    stage('Playbook Execution') {

        echo "Environment: ${environment}"
        echo "Code Base Path: ${codeBasePath}"

        dir(codeBasePath) {

            withCredentials([
                sshUserPrivateKey(
                    credentialsId: 'vm2-ssh-key',
                    keyFileVariable: 'SSH_KEY',
                    usernameVariable: 'SSH_USER'
                )
            ]) {

                sh '''
                    ansible-playbook \
                    -i inventory.ini \
                    -u "$SSH_USER" \
                    --private-key "$SSH_KEY" \
                    site.yml
                '''
            }
        }

        echo "Ansible playbook execution completed."
    }

    stage('Notification') {

        slackSend(
            channel: slackChannel,
            message: "${actionMessage} Environment: ${environment}"
        )
    }
}