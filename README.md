# Adaptive Lights
A small home automation that changes the color of my room's smart bulb to match the album art color of the song that is being played on my phone currently. This was made using the SmartThings API and an Android client app to detect the color of the currently playing song's album art.

### Why was it created?
I had nothing to do on a gloomy Saturday morning, hence decided to this small project.

### Demo
[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/OwyQGpSHPnM/0.jpg)](https://www.youtube.com/watch?v=OwyQGpSHPnM)

### Setup
- I am using [this](https://www.amazon.in/gp/product/B07XD8G2HR/ref=ppx_yo_dt_b_asin_title_o01_s00?ie=UTF8&th=1) smart bulb. This is a Phillips smart bulb (not one of those hue bulbs). It can be operated through the [WiZ](https://play.google.com/store/apps/details?id=com.tao.wiz&hl=en_IN&gl=US), but you can also control this bulb using Samsung [SmarThings](https://play.google.com/store/apps/details?id=com.samsung.android.oneconnect&hl=en_IN&gl=US) app. Since this can be controlled using SmartThings, we are using the SmartThings API to control this bulb.
- I have setup the SmartThings app to control this bulb.
- I have created a SmartThings developer account and logged in it using my Samsung account.

### Technical setup
- When you have created the SmartThings developer account, you will be given an access token. This access token will be used to access the SmartThings API, where we have to include it in our HTTP request headers as bearer token. Looks like this is how they are able to identify our device and show the devices that we have connected to our SmartThings app.
- You can list all your connected devices by making a GET request to the following URL: `https://api.smartthings.com/v1/devices/. Do not forget to include the access token in the header.
- You can list all the components of a device by making a GET request to the following URL: `https://api.smartthings.com/v1/devices/{deviceId}/components`.
- In order to control a device, you have to make POST requests to the following URL: `https://api.smartthings.com/v1/devices/{deviceId}/commands`. Now you have to tinker around with the payload to get the desired effect. For my bulb, I was not getting any value for color, I was only getting the values for hue and saturation. So I had to use the hue and saturation values to change the color of the bulb.
- I had to use the following payload to change the color of the bulb: `[{"component":"main","capability":"colorControl","command":"setColor", arguments: [{hue: 50, saturation: 50}]  }]`. In Postman, you have to use this as body in `raw` format. For you, you may have to use other capabilities to change the color of the bulb. You can find detailed reference of the capabilities [here](https://developer-preview.smartthings.com/docs/devices/capabilities/capabilities-reference).
- If you are able to setup and use the API correctly, you should be able to control the bulb by simply making a POST request from __any device__. And by any device, I mean any development device that you have access to where you can make the API calls using the access token you got earlier.
- We will make this POST request from our Android app.
- Be sure to check more about the [SmartThings API](https://smartthings.developer.samsung.com/develop/guides/smartthings-api/smartthings-api-overview/) to control the bulb.

### Android client
- Now that we can control the bulb by making a POST request, we can continue to design our Android client app.
- In order to access the song's album art for the song that is currently playing on the phone, we will use the MediaSessionManager class.
- In order to detect what song is being played, we will use a NotificationListenerService class. This will also allow us to know when the song is changed, so that we can change the color of the bulb accordingly. For this, we will have to explicitly grant the app the permission to listen to the notifications. This is a special access permission that is required to listen to the notifications, which can be found as `Notification Access` in the settings app.
- Once we have the album art, which is a bitmap, we can pass it to the Palette class to get the dominant color of the album art.
- Once we have the dominant color, we can use the color to change the color of the bulb.
- As of now, there is a check that only makes the app only work with Spotify. If you want to make it work with other apps, you can simply add the app's package name to the `supportedPackages` array. You can also remove the check by removing the `if` statement, but our process will be hit for every notification change, including the ones from non-music apps. Although there is a check in the code that avoids re-applying the previous color, but it would still detect the album art and generate the dominant color.
- I have tried to document the core parts of the app as much as possible, and provided explanations wherever necessary, which can help you ubnderstand the code.

### Can I use this for other devices?
The way this app is designed, will work for the bulb that I have setup. If you have the same bulb you can refer to this app to change the color of the bulb. Note that in order to identify your device, you have to setup the SmartThings API properly, for which an unique access token will be given to you that you have to use in the header of the request. In this app, the access token and the device ID will be hardcoded in the `local.properties` file, which is not tracked by git. This values are accessed in the source from `BuildConfig` class. So you have to properly fill out those fields in the `local.properties` file. The fields are `ST_ACCESS_TOKEN` and `ST_DEVICE_ID`. The purpose of this project is to show a demo of the functionality, and if you have the same setup as I do, you can clone this project, and build the app with the above modifications, and then run the app. You can also use this source as base to do stuff specific to your setup. Remember, if you are able to setup the SmartThings API correctly, your main job is done.