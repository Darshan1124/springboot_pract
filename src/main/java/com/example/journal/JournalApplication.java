package com.example.journal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JournalApplication {

	public static void main(String[] args) {
		SpringApplication.run(JournalApplication.class, args);
	}

}



//Yes. Think of it as two places:
//
//SonarQube side = create/bind the project, set the quality gate, and enable GitHub integration for PR decoration.
//GitHub side = run the scanner in a workflow when a PR is opened or updated. SonarQube only gets the analysis results when that workflow runs.
//
//Exact flow
//1) In SonarQube: create or import the project
//
//		In SonarQube, you first create the project or import your GitHub repository so the project is bound to GitHub. SonarQube says PR decoration is supported for bound projects, and existing manual projects can also be bound if GitHub integration is set up.
//
//2) In SonarQube: choose the quality gate
//
//Attach the project to a quality gate in SonarQube. For pull request analysis, SonarQube uses only the new code conditions from the quality gate. The built-in Sonar way gate is the default recommendation.
//
//3) In SonarQube: make sure GitHub integration is enabled
//
//At the project level, SonarQube can show analysis summaries in GitHub PRs and can also be used with branch protection checks. SonarSource says you can configure this under Project Settings > General Settings > DevOps Platform Integration and, if needed, disable the analysis summary there.
//
//4) In GitHub: store the Sonar token and server URL
//
//In your GitHub repo, add a secret named SONAR_TOKEN and a variable named SONAR_HOST_URL. SonarSource documents these as the credentials used by the GitHub Actions workflow.
//
//		5) In GitHub: create the workflow file
//
//Put the workflow in .github/workflows/build.yml. SonarSource’s example triggers on both push and pull_request events, with PR types opened, synchronize, and reopened.
//
//A normal Java example inside that workflow is:
//
//name: Build
//on:
//push:
//branches:
//		- main
//pull_request:
//types: [opened, synchronize, reopened]
//
//jobs:
//build:
//runs-on: ubuntu-latest
//steps:
//		- uses: actions/checkout@v4
//with:
//fetch-depth: 0
//		- name: Build and analyze
//env:
//SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
//SONAR_HOST_URL: ${{ vars.SONAR_HOST_URL }}
//run: mvn -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar
//
//SonarSource’s docs show the same pattern: checkout with full history, then run the scanner with the token and host URL. They also note that shallow clones should be disabled for better analysis relevance.
//
//6) For PR analysis, let GitHub Actions detect the PR automatically
//
//With GitHub Actions, SonarScanners can automatically detect branches and pull requests, so you usually do not need to manually pass sonar.pullrequest.key, sonar.pullrequest.branch, and sonar.pullrequest.base.
//
//7) Optional: block merging when SonarQube fails
//
//		In GitHub, go to Settings > Branches > Branch protection rules and require the SonarQube Code Analysis status check before merge. SonarSource documents that exact status check name.
//
//Where each thing lives
//
//In SonarQube UI
//
//Create/import the project
//Bind GitHub repo to the project
//Set the quality gate
//Enable GitHub integration / PR decoration
//View PR analysis results and issues
//
//In GitHub repo
//
//		.github/workflows/build.yml
//Repository secret SONAR_TOKEN
//Repository/org variable SONAR_HOST_URL
//Branch protection rules if you want merge blocking
