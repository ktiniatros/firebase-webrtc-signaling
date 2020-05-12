# WebRTC with Firebase Signaling

Add to your server build.gradle dependencies:
dependencies {
  implementation 'com.google.firebase:firebase-admin:6.12.2'
}

Create a Firebase project and initialize the SDK with an authorization strategy that combines your service account file together with Google Application Default Credentials.

Firebase projects support Google service accounts, which you can use to call Firebase server APIs from your app server or trusted environment. If you're developing code locally or deploying your application on-premises, you can use credentials obtained via this service account to authorize server requests.

To authenticate a service account and authorize it to access Firebase services, you must generate a private key file in JSON format.

To generate a private key file for your service account:

In the Firebase console, open Settings > Service Accounts.

Click Generate New Private Key, then confirm by clicking Generate Key.

Rename the json to the name of your database (the substring betweem https:// and .firebaseio.com/ (if you don't do that, you will have to change the logic of loading and initiating the database sdk) and place it in your resources folder.
Set the name of your database as an environment variable FIREBASE_DATABASE_NAME, eg: export FIREBASE_DATABASE_NAME=fdatab-wf4he 

## Deployment to Heroku
1. Add ```task stage {
              dependsOn installDist
          }``` in your `build.gradle`
2. Don't define any port in your `application.conf` (heroku does it for you). Your port configuration should be like ```deployment {
                                                                                                                          port = ${PORT}
                                                                                                                      }```
3. Make sure the Procfile is in git (like the one in this repo)
4. Add an `app.json` in git, just like in this repo
5. Install heroku CLI (follow the guide: https://devcenter.heroku.com/articles/getting-started-with-gradle-on-heroku#deploy-the-app)
6. After you have committed everything, run `git push heroku master`
