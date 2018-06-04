# web_temp

A template project for both server clj and client cljs, using ring, bidi, sass4clj, figwheel.

# How to use

You prepare script as belows (e.g. file name: copytemp.sh).

```copytemp.sh
#/bin/sh
cp -r web_temp $1
mv $1/src/web_temp $1/src/$1
ag -l web_temp $1 | xargs perl -pi -e s/web_temp/$1/g
```

In parent directory of `web_temp`, run `./copytemp.sh new_app`. Then connect to cider in emacs and `user> (fig-start)` and `new_app.clj.core> (start-server)`. If you have to change the port number (default: 3450), you can run `new_app.clj.core> (start-server :port 3455)`. Finally you run `lein sass4clj auto`.

## Heroku user

You can upload to heroku `heroku create` and `git push heroku master` using [Procfile](./Procfile).

## License

Copyright © 2018 iyahoo

Distributed under the Eclipse Public License either version 1.0.
