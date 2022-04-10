# plugin

## Build
`./gradlew clean build`

## Run
```shell
./gradlew run --args='--slackChannel "C01DTCUUH55" --slackToken "xoxb-1469436098135-3165783184564-mbeMlLdEZbn8TMPvXymcd6DG" --devskillerToken "<token>"'
```

## Slack integration
https://api.slack.com/authentication/basics#start
- create an app
- add [`chat:write`](https://api.slack.com/scopes/chat:write) scope
- install app to your workspace
- copy access token and provide it for this plugin
