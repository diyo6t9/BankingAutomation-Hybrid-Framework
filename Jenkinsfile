pipeline {
    agent any

    // These names must match what you add under Manage Jenkins > Tools
    tools {
        maven 'Maven3'
    }

    parameters {
        string(name: 'TAGS',
               defaultValue: '@Sanity',
               description: 'Cucumber tag expression, e.g. @Sanity | @Negative | @BillPay | @Login or @Transfer | @BillPay and not @Negative')
        booleanParam(name: 'HEADLESS',
                     defaultValue: true,
                     description: 'Run Chrome without a visible window (needed when Jenkins runs as a service)')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run Tests') {
            steps {
                // The whole -D argument is quoted so tag expressions with spaces or brackets work
                bat "mvn clean test \"-Dcucumber.filter.tags=${params.TAGS}\" -Dheadless=${params.HEADLESS}"
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'output/**', allowEmptyArchive: true
        }
    }
}