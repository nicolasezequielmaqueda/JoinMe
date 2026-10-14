# JOINME – MOBILE FOR SOCIAL GOOD

**Course:** Introduction to Mobile Systems  
**Project:** JoinMe  
**Team:** Nico & Jaime  
**Primary development platform:** Android  
**Additional target platform:** iOS  
**Technology:** Kotlin Multiplatform + Compose Multiplatform  
**Backend as a Service:** Supabase  
**Local persistence:** Room  
**Development environment:** IntelliJ IDEA Ultimate

---

## 1. Project Idea

**JoinMe** is a mobile application designed to help Erasmus and international students take part in simple social activities with other students.

The application focuses on a very specific problem:

**A student wants to do an activity but does not have enough people to do it with.**

Instead of asking users to create social profiles, make friends, chat with strangers or join a traditional social network, JoinMe focuses directly on activities.

For example:

**Table Tennis**  
Today · 19:00  
Games Room  
1 / 2 participants  
**JOIN**

or:

**Football**  
Saturday · 17:00  
Sports Court  
5 / 8 participants  
**JOIN**

The objective is to make the first social interaction easier by giving students a clear reason to meet: doing something together.

---

## 2. Social Problem

Moving to another country for an Erasmus or international study period normally means leaving an existing social circle behind and entering an unfamiliar environment.

Student residences may already provide shared facilities such as table tennis tables, common rooms, football courts, board games, projectors, pool tables or table football.

However, access to these facilities does not automatically mean that a student has someone to use them with.

Two simple examples illustrate the problem.

### Example 1 – Table Tennis

A newly arrived Erasmus student wants to play table tennis in the residence.

The table is available, but table tennis requires another player. The student has only recently arrived and does not yet know who might want to play.

With JoinMe, the student could create:

**Table Tennis – Today at 19:00 – Games Room – 1 more player needed**

Another student can discover the activity and join.

### Example 2 – Football

A student wants to organise a football match, but currently only has four people and needs eight.

Instead of contacting many people individually, an activity can be created:

**Football – Saturday at 17:00 – Sports Court – 4/8 participants**

Other students can join until the required number of participants is reached.

The problem JoinMe addresses is therefore not the lack of activities, but the difficulty of finding other students who want to participate in the same activity at the same time.

---

## 3. Evidence

Research concerning international university students identifies loneliness, social isolation and difficulties developing social connections as challenges that can occur when adapting to a new country.

Recent research has also found positive relationships between peer social interaction, students' sense of belonging and psychological well-being among international students. Other research has specifically explored interventions designed to improve social connectedness among international university students.

JoinMe does **not** claim to solve loneliness, improve mental health or prove social effectiveness for international students as a population.

Its goal is deliberately narrower:

**reduce one practical barrier to social interaction by making it easier to find people for simple shared activities.**

---

## 4. Target User Group

The primary target group is:

**Adult Erasmus and international students living in a student residence or shared university accommodation.**

The application is particularly relevant for recently arrived students who have not yet established a large social group.

The assessed prototype will not target children and will not require sensitive personal information.

---

## 5. Proposed Solution

JoinMe allows students to:

- discover available activities
- view activity information
- join or leave an activity
- create an activity
- manage activities they created
- view activities they have joined

The application is activity-oriented rather than profile-oriented.

The central concept is:

**Find something to do together, rather than searching for people to meet.**

---

## 6. Core User Stories

### US1 – Discover

> As an Erasmus student, I want to see available activities so that I can find something to do with other students.

### US2 – Join

> As an Erasmus student, I want to join an activity so that I can participate without already knowing the other participants.

### US3 – Create

> As an Erasmus student, I want to create an activity and specify when, where and how many people are needed so that other students can join me.

These three user stories define the main scope of the application.

---

## 7. Main End-to-End User Flow

The main assessed flow will be:

**Open JoinMe → Browse Activities → Select Activity → View Details → Join Activity → Activity appears in My Activities**

Example:

**Activities → Table Tennis → Activity Detail → Join → 1/2 becomes 2/2 → FULL → My Activities**

The joined activity must remain stored after the application is closed and restarted.

A secondary flow will be:

**Activities → Create Activity → Select type → Set date/time, location and participants → Create → Activity appears in Activities and My Activities**

---

## 8. Main Screens

The planned application contains four principal areas.

### 1. Activities

Main screen showing available activities.

Example categories:

- All
- Sports
- Games
- Social
- Today

Each activity card will show:

- activity type
- date/time
- location
- participants
- current status

### 2. Activity Detail

Shows:

- activity name
- date and time
- location
- number of participants
- optional short description
- current status
- Join / Leave action

### 3. Create / Edit Activity

The user can create an activity by selecting:

- activity type
- date
- start time
- approximate duration
- location
- maximum participants
- optional short description

### 4. My Activities

Shows activities:

- created by the user
- joined by the user
- upcoming
- completed

The visual prototype will be implemented directly using **Compose Multiplatform**, allowing the same UI code to evolve into the final application instead of creating a disposable prototype.

---

## 9. Activity Types

To make activity creation fast and simple, JoinMe will provide predefined activity types.

### Sports

- Football
- Padel
- Badminton

### Games

- Table Tennis
- Pool
- Table Football
- Darts
- Board Games
- Cards

### Social

- Movie Night

### Other

A custom activity can be created if the required activity is not available in the predefined list.

---

## 10. Locations

JoinMe will not require GPS or precise user location.

Instead, common residence locations can be selected from predefined options such as:

- Common Room
- Games Room
- Sports Court
- Cinema Room
- Garden
- Entrance
- Other

This keeps the interaction simple while avoiding unnecessary collection of precise location information.

---

## 11. Activity States

Activities will have simple and explicit states:

**OPEN**  
Other users can still join.

**FULL**  
The maximum number of participants has been reached.

**CANCELLED**  
The creator cancelled the activity.

**COMPLETED**  
The activity date has passed.

The activity creator counts automatically as the first participant.

Example:

A user creates a two-player Table Tennis activity.

The initial state is:

**1 / 2 – OPEN**

When another user joins:

**2 / 2 – FULL**

---

## 12. Additional MVP Behaviour

In addition to the three core user stories, the MVP may include:

- Leave an activity
- Edit an activity created by the user
- Cancel an activity created by the user
- Filter activities
- Display upcoming and previous activities
- Warn about conflicting activity times
- Remind users about an upcoming activity

These features extend the central flow without turning JoinMe into a social network.

---

## 13. Scope

### IN SCOPE

- Browse activities
- Activity details
- Create activities
- Edit own activities
- Cancel own activities
- Join activities
- Leave activities
- My Activities
- Predefined categories
- Simple filters
- Predefined locations
- Activity states
- Persistent local data
- Remote data synchronization
- Offline access to previously stored information
- Network loading/error states
- Simple local activity reminder
- Android application
- iOS build using the shared Kotlin Multiplatform codebase

### OUT OF SCOPE

- Chat
- Private messaging
- Friends
- Followers
- Social-media profiles
- Photographs
- Camera
- GPS
- Maps
- Precise location tracking
- Payments
- Ratings
- Reviews
- Likes
- AI recommendations
- Matching algorithms
- Complex authentication
- Continuous tracking

These exclusions are deliberate to ensure the project remains achievable within the allocated workload.

---

## 14. Technical Approach

JoinMe will be developed using:

**Kotlin Multiplatform (KMP)**  
Shared application logic between Android and iOS.

**Compose Multiplatform**  
Shared user interface components and screens.

**Supabase**  
Remote Backend as a Service used to store and synchronize shared activity information.

**Room**  
Local persistent database used to store activities on the device.

The intended data flow is:

**Compose UI → ViewModel → Repository → Room / Supabase**

Supabase represents the remote shared data source.

Room represents the local persistent copy.

When connectivity is available, data can be synchronized with Supabase.

When connectivity is unavailable, JoinMe can continue displaying previously stored information from Room.

---

## 15. Target Platforms

Development and testing will initially focus on **Android**.

This allows the team to implement and validate the complete application flow before introducing additional platform-specific complexity.

Because the project uses Kotlin Multiplatform and Compose Multiplatform, the shared application will also target **iOS**.

Once the Android version is stable, the iOS target will be built and tested using:

**macOS + Xcode**

The Android implementation remains the primary assessed development target unless otherwise required, while the iOS version demonstrates the multiplatform nature of the project.

---

## 16. Networking and Offline Behaviour

JoinMe will use Supabase to retrieve and update activity data.

The application should explicitly demonstrate:

**Loading**

The app is requesting current activities.

**Success**

Current activities are received and stored locally.

**Failure / Offline**

The remote request cannot be completed.

In this situation, JoinMe will display previously stored information with a message such as:

> You're offline. Showing saved activities.

This allows the application to remain useful even when connectivity is temporarily unavailable.

---

## 17. Persistent Data

Meaningful information must survive an application restart.

For example:

1. A user joins Table Tennis.
2. The activity is stored locally.
3. The application is completely closed.
4. The application is opened again.
5. Table Tennis still appears under My Activities.

Room will provide the local persistent storage required for this behaviour.

---

## 18. Mobile Capability

JoinMe will use **local notifications** as its justified mobile capability.

Example:

> Table Tennis starts in 30 minutes.

Notifications support the purpose of JoinMe because users may create or join an activity several hours or days before it begins.

If notification permission is unavailable or denied, the main application remains fully functional.

The fallback behaviour will be:

> Notifications are disabled. Upcoming activities are still available in My Activities.

No core feature will depend on notification permission.

---

## 19. Accessibility Requirements

Two explicit accessibility requirements will be implemented and tested.

### Requirement 1 – Scalable Text

The complete main flow must remain usable when the system text/font size is increased.

Important information and actions must not become hidden or unusable.

### Requirement 2 – Information Must Not Depend Only on Colour

Activity states must always include explicit text.

For example:

**OPEN**  
**FULL**  
**JOINED**  
**CANCELLED**

Colour may reinforce the status but will never be its only representation.

Interactive elements will also use appropriate labels and sufficiently large touch targets.

---

## 20. Data, Privacy and Safety

JoinMe is intended for adult university students.

The prototype does not require:

- health information
- precise location history
- financial information
- information about children
- private conversations
- sensitive personal information

During development and testing, synthetic activity data can be used.

A minimal technical user identifier may be used internally to distinguish created and joined activities without requiring a complex public user profile.

Locations will refer to shared or predefined places rather than private addresses or continuously tracked positions.

---

## 21. Social Impact Limitations

JoinMe may make it easier for students to discover opportunities for social interaction.

However, the project will not claim that JoinMe:

- solves loneliness
- improves mental health
- guarantees friendships
- improves Erasmus integration for the wider student population

The usability evaluation conducted for the course assesses the usability of the prototype, not the effectiveness of the application as a social intervention.

---

## 22. Planned Usability Evaluation

Two voluntary usability sessions will be conducted with consenting adult peers who are not members of the development team.

Example tasks may include:

**Task 1**

> You want to play table tennis this evening. Find an available activity and join it.

**Task 2**

> You want to organise a football activity for eight people tomorrow. Create the activity.

Anonymous observations will be recorded.

The final evidence will include:

- usability problems observed
- limitations of the sessions
- at least two improvements or justified decisions not to make a change

---

## 23. Planned Testing

At least three meaningful automated logic tests will be implemented.

Examples include:

- a participant can join an activity with available capacity
- a participant cannot join an activity that is full
- joining an activity updates the participant count and changes its state to FULL when capacity is reached
- leaving an activity reduces the participant count
- conflicting activity times are correctly detected

A short manual scenario matrix will also cover:

- normal user flow
- network failure/offline mode
- application restart
- increased accessibility font size

---

## 24. Team and Development Process

The project team consists of:

**Nico**  
**Jaime**

Both members will contribute directly to the implementation.

Development will use a shared Git repository with small commits and feature-based work.

Individual contributions will be identifiable through:

- Git commits
- branches / pull requests where appropriate
- feature ownership
- contribution notes

Both team members should understand the complete application architecture and main data flow in preparation for the individual defence.

---

## 25. Prototype Strategy

The M1 prototype will not be developed as a separate disposable HTML, website or design project.

Instead, the team will create the actual **Kotlin Multiplatform + Compose Multiplatform project**.

For M1, it will initially contain:

- real navigation
- reusable Compose UI
- sample/fake activities
- the four main screens
- representative states such as OPEN and FULL

It will initially exclude:

- complete Supabase integration
- complete Room persistence
- final networking
- final notifications

The same prototype code will then progressively become the M2 working application.

This reduces duplicated work and ensures that effort invested during M1 contributes directly to the final product.

---

## 26. MVP Success Criteria

The main project objective is achieved when a user can:

1. Open JoinMe
2. Browse available activities
3. Select an activity
4. Join it
5. See its participant count update
6. Find it under My Activities
7. Close the application
8. Reopen it
9. Still see the joined activity

The application must also provide useful behaviour when network connectivity is unavailable.

---

## 27. Final Project Principle

JoinMe will prioritise one polished, understandable and reliable activity flow over a large number of partially implemented social features.

The project is not intended to become a social network.

Its purpose is simple:

**Help a student find people to do something with.**