# Drivify
Android app to create custom Spotify playlist based on user preference, duration of route, and advanced Spotify track details


## Table of Contents
1. [Overview](#Overview)
2. [Product Spec](#Product-Spec)
3. [Wireframes](#Wireframes)
4. [Schema](#Schema)

## Overview
### Description
The app will use google maps and spotify to try to deliver a playlist and music experience that is dynamic to the drive.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Music/Navigation
- **Mobile:** Mobile only unless maybe a webapp that can build ahead of time
- **Story:** Creates a fun semirandom experience that will help the user enjoy their commutes in a new way. It will also allow them to have a dynamic experience that doesnt require and action while driving.
- **Market:** Any person riding in a vehicle or on a commute could use this app.
- **Habit:** People are using this when they commute from one place to another.
- **Scope:** V1 log in and access both API create a playlist from a genre ID or users likes that fits the length of the drive. V2 Use geolocation data to adjust volume and try to modify playlist on the fly. Also use and SDK page to track drives the user has went on and rate them V3 Try to analyze songSimplifieds based on the songSimplified analysis api for spotify and implement an algorithm that tries to place songSimplifieds in some kind of order based on what the user wants. (Could put intense songSimplifieds towards the start or end?)

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

[x] User Register/Log in to driving app
[x] User can log into spotify
[x] User can enter current location and destination to get route time
[x] User can specify a genre or songSimplified that songSimplifieds selected can be based off using spotify seed
[x] User recieves a link to a playlist that is genre specific and matches route time well

**Optional Nice-to-have Stories**

[x] User can view previous drives via google maps sdk
[x] User can choose whether the playlist is gaining, waining, or peaking in intensity
[x] User's speed is tracked and used in an algorithm to determine volume

### 3. Demo
- **Algorithm Testing**
Using data sets from https://github.com/Intelligent-Vehicle-Perception/Intelligent-Vehicle-Perception-Based-on-Inertial-Sensing-and-Artificial-Intelligence
**Test1:**
<img src='https://github.com/TravisGibbs/Drivify/blob/master/demonstration/Test1.png?raw=true' title='Test1' width='536' height='895' alt='test1' />
**Test2**
<img src='https://github.com/TravisGibbs/Drivify/blob/master/demonstration/Test2.png?raw=true' title='Test2' width='536' height='895' alt='test1' />

