Earshot is an application that can recognize any audio track playing in the background within Earshot and give information about the song. It also searches the song in Spotify to retrieve its metadata.

User has the choice to login into his spotify account and add the track to a playlist created by the application itself called “Earshot”.



Features:-

1) Audio Recognition for 40 million tracks using Doreso.

2) Simple yet powerful and highly intuitive user experience based on material design.

3) Built in capability to create a playlist in Spotify and add tracks to it.

4) User does not need to be logged in to use the application, only needs to login to add tracks to spotify.

5) User does not need to login every time he opens the application. Login details are stored unless he logs out.

6) Authentication in Webview making the application secure.



Known bugs:-

1) Login/Logout is controlled by spotify in a web view and sometimes has issues.



Steps to follow:-

1) Open the application. Click on the mic icon in the middle of the screen. When the application starts recording the audio, it shows animation around the mic icon indicating that it is recording the audio. 

2) As soon as it recognizes the track, it searches the track in Spotify, and displays the information about the song.

3) User can add the track to the playlist “Earshot” by clicking the + icon. If Earshot playlist does not exist, a new one is created.

4) If the user is not logged in to Spotify, the application asks the user to login.

5) A toast is displayed indicating that the song has been added.



Libraries Used:-

1) Volley - https://github.com/mcxiaoke/android-volley

2) Spotify Authentication - https://github.com/spotify/android-sdk

3) Doreso - http://developer.doreso.com/

4) Circular reveal - https://github.com/ozodrukh/CircularReveal (Source code has been added to the project because this artifact was not present in maven central)

5) NineoldAndroids - https://github.com/JakeWharton/NineOldAndroids

6) Picasso-Square - http://square.github.io/picasso/ 



Limitations:-

1) Does not recognize all the tracks.

2) Authentication token expiration not handled.(user has to log out manually)

3) All tracks added to playlist Earshot. User does not have choice to add to another playlist.

4) Preliminary error handling for network errors.

5) Same track can be added multiple times.



Future scope:-

1) Give user choice to add tracks to any of his playlists.

2) Give user choice to create playlists in Spotify and add tracks to it.

3) Maintain history of the songs that the user has searched.

4) Support more music services.

5) Implement Facebook chat head like interface for easy accessibility.

6) Add other music features such as play the song, lyrics support, artist info, etc.


Submitted by:
Nehal Chaudhary
Pratik Sharma
