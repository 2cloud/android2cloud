## 2cloud Android Client

### About

2cloud is a free, decentralised, open source project to try and make sharing 
content between browsers and devices as seamless and effortless as possible. An 
up-to-date list of devices and browsers supported by the project is available at 
http://www.2cloudproject.com/clients

This is the Android client that was launched originally in July of 2010. It has 
been 
[deprecated](http://blog.android2cloud.org/2011/07/you-cant-teach-old-dog-new-tricks.html) 
in an effort to free our team to focus on the future, instead of the past. This 
repository will remain here for archival purposes, as well as to make the code 
available to any of the users whose phones can't run the [newer version](https://www.github.com/2cloud/Android).

### Installation Instructions

We tried to make installation and modification as easy as possible. Simply download 
the code (either using git or the download button on Github) and build it like any 
other Android application. You'll need the [SDK](http://developer.android.com/sdk/index.html), 
but Google has some [helpful resources](http://developer.android.com/resources/faq/commontasks.html#neweclipseandroidproject) 
for people who need help getting started.

### Where to Get Help

We try to maintain a presence with our users. To wit, we have:

* A Tender support site (the best way to get help with "it's not working"): http://help.2cloudproject.com
* An announcement mailing list (the best way to stay up-to-date on downtime and changes): http://groups.google.com/group/2cloud-announce
* A discussion mailing list (the best way to talk to other users and the team): http://groups.google.com/group/2cloud
* A development mailing list (the best way to stay on top of API changes): http://groups.google.com/groups/2cloud-dev
* A beta mailing list (if you want to help test buggy software): http://groups.google.com/group/2cloud-beta
* A Twitter account (the best way to stay on top of new releases and other updates): http://www.twitter.com/2cloudproject
* A Facebook page (the second best way to stay on top of new releases and other updates): http://www.facebook.com/2cloud
* A website (for a bunch of other links and information): http://www.2cloudproject.com
* A blog (for lengthier updates and explanations): http://blog.2cloudproject.com
* A Github account (where all our source code and issues reside): https://www.github.com/2cloud

If you don't use _any_ of those... you're kind of out of luck.

### Contribution Guidelines

The quickest, easiest, and most assured way to contribute is to be a beta tester.
Simply join the [mailing list](http://groups.google.com/group/2cloud-beta) and 
wait for a new beta to be released. Try and break it. Submit feedback. Wash, 
rinse, repeat.

If you're interested in contributing code, we use different guidelines for each 
part of our app. This is driven by necessity; you can't use JUnit on Javascript, for 
example. Unfortunately, we're still in the process of defining our guidelines 
for Android. This project has been retired, but we'll still accept pull requests 
for it provided you explain what you changed, why you changed it, and why the app 
is better that way. In our future version, expect to see unit tests with JUnit and 
Checkstyle implemented and required for pull requests.

The best way to figure out what's on our to-do list is to look at the 
[issue tracker](https://www.github.com/2cloud/android2cloud/issues) or ask on the 
[dev mailing list](http://groups.google.com/group/2cloud-dev). Whatever you work 
on should be something _you_ want to see implemented, though.

### Contributors

2cloud is an open source application. It is technically "owned" by [Second Bit LLC](http://www.secondbit.org), 
but all that really means is they take care of the mundane administrative and 
financial stuff. The team behind 2cloud is separate from the Second Bit team 
(despite some overlap). The 2cloud team is as follows:

* Paddy Foran - Lead Developer - [@paddyforan](http://www.twitter.com/paddyforan) - http://www.paddyforan.com/
* Dylan Staley - UI/UX Lead - [@dstaley](http://www.twitter.com/dstaley) - http://www.dstaley.me
* Tino Galizio - Project Manager - [@tinogalizio](http://www.twitter.com/tinogalizio) - http://www.secondbit.org/team/tino

They're pretty friendly. Please do get in touch!

### Credits and Alternatives

One of the great parts about being an open source project is how often we get to 
stand on the shoulders of giants. Without these people and projects, we couldn't 
do what we do.

* blog.notdot.net
* Signpost, a (sadly discontinued) OAuth library for Java that suits our needs wonderfully
* Chrome to Phone (Inspiration)
* The sample In-App Payments app was largely cannibalised for our In-App payments system

There are some alternatives to 2cloud out there, and we encourage you to try them 
out. Use what works best for you. You can find an up-to-date list on 
[our website](http://links.2cloudproject.com/competition).
