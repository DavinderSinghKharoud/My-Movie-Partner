# MY MOVIE PARTNER
## Tools And Libraries Used:

1. Firebase Realtime Database
2. Google Maps API
3. Retrofit Library
4. Firebase Cloud Messaging
5. Recycler View
6. Navigation Bar
7. Spinner
8. Radio Group
9. Search View
10. Animations
11. Material Design
and more.

___
* ### Application Functions

```
a) Log In
```
In order to log in, user needs to enter the same email id and password that he has entered during sign up. If entered text in both fields or one of the fields is invalid or empty, a toast message will appear telling the user what went wrong.

<img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/Login.png" width="180" height="370">

```
b) Sign Up And Forgot Password
```
> When user is using the app for first time, he needs to register or sign up by entering his name, email id, creating a password for the account, his or her gender. After that, all the details that the user entered will be saved in firebase database. The user needs to enter all the text fields in order to register otherwise a toast message will appear, in case, one or more fields are empty or entered text is invalid. Once user is registered, then email address need to be verified before login.

<img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/Register.png" width="180" height="370"><img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/ForgotPassword.png" width="180" height="370">


```
c) Homepage
```
>When user is successfully logged in, the application homepage will open where user can see the post of other people, in which, he or she can see other person name, his title and description of post. Furthermore, he can also see his available date and time and location to meet including the user gender.
Moreover, there can be thousands of posts like that, and user can easily scroll through each post. Each post will have two more features, at every post there will be two more icons, a messaging icon and a sharing icon.


<img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/list.png" width="180" height="370"><img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/messageEleana.png" width="180" height="370"/><img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/share.png" width="180" height="370"/>

* Messaging Icon

When user press this icon or on the post , a chat screen of that specific user will open. Then user can send messages to that user and also can receive at the same time. User can also see the status whether he is online or offline and get to know, whether messages are read or not. Here both users can plan the movie or a hang out through messaging.

* Sharing Icon

Whenever the user clicks this icon on any specific post then it will enable the user to share that particular post via various social platforms.



```
d) Create Post
```
>User can create and upload post like other users. This feature will be in Navigation bar and by opening it, user can see Create post option in it. 
>When clicking it, five empty text fields will open that will require user to fill it in order to create the post. The five text fields are:

<img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/createPost.png" width="180" height="370"/> <img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/createPostUser.png" width="180" height="370"/><img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/selectDate.png" width="180" height="370"/><img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/selectTime.png" width="180" height="370"/><img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/selectMaps.png" width="180" height="370"/>


* Title of post
* Description of post
* Date to meet
* Time to meet
* Location to meet(User can also select the location from the map)
After entering these details, user can click on post button and then he can see his post on homepage of the app.
```
e) Messages
```
>Like create post, this feature can also be accessed by opening navigation bar. After clicking it, user can see the friends list. All the messages shared between friend by clicking on the friend from the list. 

<img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/messageList.png" width="180" height="370"/><img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/messageChat.png" width="180" height="370"/><img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/notification.png" width="180" height="370"/>

```
f) Share Application
```
>User can also share the application on various social platforms so that finding a movie partner will not be that difficult. This option will also be in Navigation drawer.

<img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/share.png" width="180" height="370"/>

```
g) Search Partner
```
>To find a partner to go for a movie, user can search in the homepage of the app by using search filters like username, title of post, gender and location. After this, the application will show the user the posts according to his preferences selected.

<img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/byUser.png" width="180" height="370"/><img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/byName.png" width="180" height="370"/><img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/byGender.png" width="180" height="370"/><img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/byLocation.png" width="180" height="370"/>

```
h) Profile settings
```
>In profile settings, user will see four options:

<img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/profileList.png" width="180" height="370"/>

* Update account

User will have the option to edit his name, email id, create new password, change gender and save this info by pressing the save button. After that his information will be updated in the database and user can see his changes in the app.


<img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/manageAcco.png" width="180" height="370"/>

* Change profile picture

Whenever user click this option, a new screen will open with a user current profile picture and a button named choose photo.
When user clicks this button, he can access his phone gallery where he can choose any picture and use it as a profile picture.

<img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/changePic.png" width="180" height="370"/>

* Your Posts

All the posts that the user has posted can be viewed in this option. Here he can edit, delete, or share his post.

<img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/ownPost.png" width="180" height="370"/>

* Delete account

User can simply delete his account by clicking on this option. All his details including his posts and picture will be removed from the database.

<img src="https://github.com/DavinderSinghKharoud/ImagesMyMoviePartner/blob/master/deleteAcc.png" width="180" height="370"/>

```
i) Log out
```
>This feature is understandable. Since user is logged in, there needs to be log out as well.

</p>

___


## Some problems that you can face:
1. When to add the object in the recyclerView adapter, if there are more than two firebase references.
2. Carefully, adding and removing the firebase event listeners.
3. Sending notification to another user using retrofit library, and firebase cloud messaging.
