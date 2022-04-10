# plugin

## Build
`./gradlew clean build`

## Run
```shell
./gradlew run --args='--devskillerToken "<token>" --slackToken "<token>" --slackChannel "<channel>"'
```

## Publish
to Maven local \
`./gradlew publishToMavenLocal`

## Slack integration
https://api.slack.com/authentication/basics#start
- create an app
- add [`chat:write`](https://api.slack.com/scopes/chat:write) scope
- install app to your workspace
- copy access token and provide it for this plugin
