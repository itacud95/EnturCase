# Entur case 

### Icons
Icons are fetched from: https://fonts.google.com/icons

### Tests
Tests can be executed with `./run-tests.sh` (will execute on device).

### Notes
#### Fetching data
Given the limited time for this case, I ended up taking the shortest path for fetching the data.   
I do not have any experience using `GraphQL`, and therefore I am a bit unsure if I setup the schema for that correctly.   
I also ended up using `okhttp` to fetch the stop places for a location, since this was exemplified in the task (thank you!).
#### UI
I have used `Jetpack Compose` for all UI related elements, trying to keep `MainActivity.kt` as clean and simple as possible. 
#### Design pattern
I have used the design pattern `Model-View-ViewModel`
- UI remains free of business-logic
- data loading is done in a repository
- viewmodels used as a bridge between the business and UI
#### Remaining tasks
- **The app us unusable without location permission**; While I have added a 'lock' asking the user for permission, the should not be unusable without permissions. A welcome and about screen should be presented. Maybe the user could search their stop place using a query instead. 
- **Handle connectivity and location**; The app does not handle missing connectivity and location, this should be added to ensure the user can still use the app. The current implementation will just present a blank screen, this is not ideal. 
- **Support older apis**; I am using minSdk `26`, and the app is missing a way to support older models. 
- **Permission change**; If the app loses location permission after the initial screen, the app will not request the permission again before the app is restarted (or the home screen is entered).
- **Location accuracy**; The location fetching can and should be improved. For example using `requestLocationUpdates` for continuos update of location. 
- **Use more of available data**; I see there is ALOT more data to play with, both for the stops and departures. This is very bare-bones, and I would like to make use of more of it. 
- **Time to departure**; Building on the last point, the departures should also display the time for when it leaves, not just the remaining minutes. 

### GraphQL
I used the following command to setup the graphQL schema.
```bash
./gradlew downloadApolloSchema \
  --endpoint="https://api.entur.io/journey-planner/v3/graphql" \
  --schema="app/src/main/graphql/com/example/myapp/schema.graphqls"
```
