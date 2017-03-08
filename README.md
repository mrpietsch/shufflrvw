# ShufflR

This repository contains the sample solutions for the exercises of the "Akka in Practice" training
course. Each commit corresponds to an exercise – simply navigate the Git history to arrive at the
state of the solutions for the respective exercise.

## cURL Cheat Sheet

```
curl -i localhost:8000/users

curl -i localhost:8000/users \
  -H 'Content-Type: application/json' \
  -d '{ "username": "jdoe", "email": "john@doe.name" }'

 curl -i localhost:8000/users -u jdoe:jdoe

 curl -i localhost:8000/users/jdoe -u jdoe:jdoe -XDELETE

 curl -i localhost:8000/shuffle -u jdoe:jdoe -d 'Hello, World!'
```
