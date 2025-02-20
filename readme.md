# Entur case 

### GraphQL
I used the following command to setup the graphQL schema.
```bash
./gradlew downloadApolloSchema \
  --endpoint="https://api.entur.io/journey-planner/v3/graphql" \
  --schema="app/src/main/graphql/com/example/myapp/schema.graphqls"
```

### Icons
Icons are fetched from: https://fonts.google.com/icons

### Tests
Tests can be executed with `./run-tests.sh` (will execute on device).
