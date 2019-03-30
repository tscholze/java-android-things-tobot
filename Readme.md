<center><img src="https://github.com/tscholze/java-android-things-tobot/blob/master/docs/app-icon.png" /></center>

# ToboT

ToboT is an [Android Things](https://developer.android.com/things/index.html) app to move a vehicle based on the [Pimoroni STS Pi](https://shop.pimoroni.de/products/sts-pi) Kit.

It also contains different other remote control methods like a website that is hosted on the vehicle or simple Google Assistant voice commands to let it move it ahead, left, right, backwards or it lets the vehicle stop.

Tobot was never be intend to be more than a great and enjoyable sparetime project. That means, no code is production-ready. 

For more information please have a look at the German posts ([What is Tobot?](https://dbudwm.wordpress.com/2018/04/04/android-things-der-anfang-von-tobot/), [Google Assistant Feature](https://dbudwm.wordpress.com/2018/05/03/google-assistant-firebase-function-database-fahrtrichtung-von-tobot-via-stimme-steuern/)) on my personal blog.

## Information

Due to the lost interest of Google in Android IoT and missing support for more modern Raspberry Pis is this project at the moment on hold. I would be happy if this project could live longer.

## Prerequisites

### Required
* [Android Things](https://developer.android.com/things/index.html) Developer Preview 7 (or maybe higher)
* Pimoroni [STS Pi](https://shop.pimoroni.de/products/sts-pi) Kit
* Pimoroni [Explorer HAT](https://shop.pimoroni.de/products/explorer-hat)

### Optional (for voice control)
* [AYI Voice Kit](https://aiyprojects.withgoogle.com)
* [Firebase account](http://firebase.google.com)


## Types of remote controls

### Build in Explorer HAT buttons
To check if the app works, it is possible to let the vehicle move by touching the capacitive buttons `1` to `4` of the Explorer HAT.

```
switch (keyCode)
{
    case BUTTON_1_KEYCODE:
        return movementRequestListener.requestMovement(MovementCommand.FORWARD);

    case BUTTON_2_KEYCODE:
        return movementRequestListener.requestMovement(MovementCommand.LEFT);

    case BUTTON_3_KEYCODE:
        return movementRequestListener.requestMovement(MovementCommand.RIGHT);

    case BUTTON_4_KEYCODE:
        return movementRequestListener.requestMovement(MovementCommand.STOP);
}

```

### Build in mobile-friendly website
If the Tobot and a browser-enabled device are in the same network, the vehicle could be controlled by using the on [Material Design Lite (MTL)](https://getmdl.io/started/) based and on the vehicle itself hosted website by surfing to `<VEHICLE-IP>:8080`.

<center><img src="https://github.com/tscholze/java-android-things-tobot/blob/master/docs/website.jpg" /></center>


### Google Assistant

You can [create a custom Action](https://developers.google.com/actions/) for the Google Assistant. The shown action is not public available.

<center><img src="https://github.com/tscholze/java-android-things-tobot/blob/master/docs/assistant.png" /></center>

Tobot uses also a Firebase Realtime database and a Firebase Function to convert the Action intend into a device-compatible database entry trigger. 

The function below is a Frankenstein-information with less to none security. Do not use this in any other case as for playing around!

```
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const DialogflowApp = require('actions-on-google').DialogflowApp;

// Initialize Firebase app
admin.initializeApp();

exports.receiveAssistantRequests = functions.https.onRequest((request, response) => {

    // Create app from request parameters
    const app = new DialogflowApp({request: request, response: response});

    function handlerRequest(app) {

        // Get argument from assistant
        const command = app.getArgument('movement-command');

        // Create and insert new data base entry for parsed command.
        return admin.database().ref('/remote_commands').push({

            command: command,
            consumerId: "42",
        }).then(snapshot => {

            app.ask(`Ok, the vehicle will move to ${command}. And now?`);
        });
    }

    app.handleRequest(handlerRequest);
});
```

The Google Assistant triggers an Action, the Action calls a Firebase Function, the function writes into a Firebase Realtime Database on which the Tobot listens for data changes. If the vehicle recognizes a database event, after some checks it will execute the new command and writes back an acknowledge flag.

<center><img src="https://github.com/tscholze/java-android-things-tobot/blob/master/docs/flow.png" /></center>


## Contributing

This is a time-by-time sparetime project for myself. That means, no contribution is necessary.

## Authors

Just me, [Tobi]([https://tscholze.github.io).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
Dependencies or assets maybe licensed differently.

## Acknowledgments

* [Qubodup](https://openclipart.org/user-detail/qubodup) App icon designer (openclipart.org)
* [Tim Messerschmidt (Google)](https://twitter.com/SeraAndroid/status/981632694692925440) For the motivating tweets
* [Francesco Azzola](https://www.survivingwithandroid.com/2017/12/android-things-gpio-pins-build-a-controlled-car.html) Android Things GPIO pins tutorial: Control peripherals
* [Google's IoT Developers Community](https://plus.google.com/u/0/communities/107507328426910012281) for helping beginners, too
