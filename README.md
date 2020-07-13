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

* User Register/Log in to driving app
* User can log into spotify
* User can enter current location and destination to get route time
* User can specify a genre or songSimplified that songSimplifieds selected can be based off using spotify seed
* User recieves a link to a playlist that is genre specific and matches route time well

**Optional Nice-to-have Stories**

* User can view previous drives via google maps sdk
* User can choose whether the playlist is gaining, waining, or peaking in intensity

### 2. Screen Archetypes

* Log in/registration page
   * Make a user profile
   * If registering also log into spotify
* Playlist making page
   * Allow input of destination and origin
   * Allow user to input a songSimplified or genre to base playlist off of
   * Return a link to a playlist
* Map and previous drives page
   * Allows users to plot previous routes or destinations and stores playlist links
* Profile Page
   * Allows users to view their profile image and to sign out or modify personal details

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* first tab - profile acivity
* second tab - playlist activity
* third tab - map activity

**Flow Navigation** (Screen to Screen)

* Login/registration page
   * Navigates to main three tab page from here
* Playlist page
   * navigates to spotify via a link from here

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
