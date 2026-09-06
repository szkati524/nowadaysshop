pipeline {
    agent any

    tools {
       
        maven 'Maven-3.9'
        nodejs 'Node-20'
    }

    environment {
        DOCKER_DRIVER = 'overlay2'
      
        DOCKER_HOST = 'unix:///var/run/docker.sock'
    }

    stages {
        stage('Checkout Source') {
            steps {
                echo '===  Pobieranie kodu z repozytorium ==='
                checkout scm
            }
        }

        stage('Backend - Unit & Integration Tests (Testcontainers)') {
            steps {
                echo '===  Budowanie i testy backendu (Spring + Testcontainers) ==='
                dir('backend') {
                    
                    sh 'mvn clean test'
                }
            }
            post {
                always {
                    
                    junit allowEmptyResults: true, testResults: 'backend/**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Frontend - Unit Tests (Vitest)') {
            steps {
                echo '===  Instalacja zależności i testy jednostkowe UI ==='
                dir('frontend') {
                    sh 'npm ci'
                    sh 'npm run test:run'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                echo '=== Budowanie obrazów dockerowych mikserwisów ==='
                dir('backend') {
                    sh 'mvn spring-boot:build-image -DskipTests'
                }
                dir('frontend') {
                    sh 'docker build -t frontend-app:latest .'
                }
            }
        }

        stage('E2E Tests (Playwright)') {
            steps {
                echo '===  Uruchomienie środowiska i testów E2E ==='
               
                sh 'docker compose up -d --build'
                
                dir('frontend') {
                  
                    sh 'npx playwright test'
                }
            }
            post {
                always {
                    echo '=== Czyszczenie środowiska E2E ==='
                    sh 'docker compose down -v'
                  
                    archiveArtifacts artifacts: 'frontend/playwright-report/**', allowEmptyArchive: true
                }
            }
        }
    }

    post {
        success {
            echo ' Projekt gotowy do wdrożenia.'
        }
        failure {
            echo ' Pipeline zakończony błędem. Sprawdź logi powyżej.'
        }
    }
}