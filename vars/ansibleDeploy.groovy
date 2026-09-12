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

    stage('User Approval') {
        def keepApproval = true

        if (keepApproval) {
            input(
                message: 'Do you want to proceed with SonarQube deployment?',
                ok: 'Proceed'
            )
        } else {
            echo "Approval stage skipped."
        }
    }

    stage('Playbook Execution') {

        echo "Executing SonarQube Ansible playbook..."

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

        echo "Ansible playbook execution completed."
    }
}