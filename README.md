ndroid_test_v1.5
Following Requirements are completed
## General Requirements
#### REQUIREMENT 1. 
In the first screen, the app has to fetch GitHub users list, parse it and display in the list.
#### REQUIREMENT 2. 
Selecting a user has to fetch users profile data and open a profile view displaying the user's profile data.
#### REQUIREMENT 3. 
The design must loosely follow the wireframe (at the bottom of this document) but you must demonstrate a high level of knowledge about best practises of Android UX and UI principles (e.g. Material design). The app must look good, work smoothly and the design must follow platform defaults.
#### REQUIREMENT 4. 
Own code logic should be commented on.
## Generic Requirements
#### REQUIREMENT 1.
Code must be done in Kotlin 1.4.x. using AndroidStudio.
#### REQUIREMENT 2.
Data must be persisted using Room.
#### REQUIREMENT 3.
UI must be done using ConstraintLayout where appropriate.
#### REQUIREMENT 4.
All network calls must be queued and limited to 1 request at a time.
#### REQUIREMENT 5.
All media has to be cached on disk.
#### REQUIREMENT 6.
Write Unit tests for data processing logic & models, Room models (validate creation & update).
#### REQUIREMENT 7.
Screen rotation change must be supported.

## GitHub users
#### REQUIREMENT 1.
The app has to be able to work offline if data has been previously loaded.
#### REQUIREMENT 2.
The app must handle no internet scenario, show appropriate UI indicators.
#### REQUIREMENT 3.
The app must automatically retry loading data once the connection is available.
#### REQUIREMENT 4.
When there is data available (saved in the database) from previous launches, that data should be displayed first, then (in parallel) new data should be fetched from the backend.

# Users list
#### REQUIREMENT 1.
Github users list can be obtained from https://api.github.com/users?since=0 in JSON format.
#### REQUIREMENT 2.
The list must support pagination (scroll to load more) utilizing since parameter as the integer ID of the last User loaded.
#### REQUIREMENT 3.
Page size has to be dynamically determined after the first batch is loaded.
#### REQUIREMENT 4.
The list has to display a spinner while loading data as the last list item.
#### REQUIREMENT 5.
Every fourth avatar's (the image - not the background!) colour should have its (image) colours inverted.
#### REQUIREMENT 6.
List item view should have a note icon if there is note information saved for the given user.
#### REQUIREMENT 7.
Users list has to be searchable - local search only; in search mode, there is no pagination; username and note (see Profile section) fields should be used when searching, precise match as well as contains should be used.

#Profile
#### REQUIREMENT 1.
Profile info can be obtained from https://api.github.com/users/[username] in JSON format (e.g. https://api.github.com/users/tawk).
#### REQUIREMENT 2.
The view should have the user's avatar as a header view followed by information fields (UIX is up to you).
#### REQUIREMENT 3.
The section must have the possibility to retrieve and save back to the database the Note data (not available in GitHub api; local database only). 

# BONUS
#### 1. Exponential backoff must be used when trying to reload the data.
#### 2. MVVM pattern should be used.
#### 3. The app has to support dark mode.

# Third-Party Libraries
#### 1. Picasso
Used to load image from web and for image caching. https://square.github.io/picasso/
#### 2. Retrofit
Retrofit turns your HTTP API into a Java interface. https://square.github.io/retrofit/A

