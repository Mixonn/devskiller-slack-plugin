# Slack notifications for Devskiller

[Devskiller](https://devskiller.com/) is a platform used to evaluate candidates' skills in the hiring process.
The test, when completed by a candidate, may be reviewed manually by the technical recruiter or automatically, depending on the hiring team's needs.
When choosing the former approach, then there exists a queue of tests that needs to be verified.
This script reads the state of this queue and pushes its details in a Slack notification, so that the reviewing team does not need to log in to Devskiller in order to monitor the queue.

## Build
`./gradlew clean build`

## Run
```shell
./gradlew run --args='--help'  # get description of the available arguments
./gradlew run --args='--devskillerToken "<token>" --slackToken "<token>" --slackChannel "<channel>" --testGroups "java,1234;python,222,333,444"'
```

## Publish to Maven local
`./gradlew publishToMavenLocal`

## Slack integration
https://api.slack.com/authentication/basics#start
- create an app
- add [`chat:write`](https://api.slack.com/scopes/chat:write) scope
- install app to your workspace
- copy access token and provide it for this plugin
