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

        echo "Checking Ansible installation..."

        sh 'ansible --version'

        echo "Executing SonarQube Ansible playbook..."

        sh '''
            ansible-playbook -i inventory.ini site.yml
        '''

        echo "Ansible playbook execution completed."
    }
}