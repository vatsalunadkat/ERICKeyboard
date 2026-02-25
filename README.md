<a name="readme-top"></a>

<!-- PROJECT LOGO -->
<div align="center">
  <h3 align="center">Ergonomic Radial Inclusive Controller Keyboard (ERICK)</h3>

  <p align="center">
    An ergonomic keyboard system for Android using swipe based chord input
    <br />
    <br />
    <a href="https://github.com/vatsalunadkat/Ergonomic-Radial-Inclusive-Chorded-Keyboard/issues">TODO - Visit App - Playstore Link</a>
    ·
    <a href="https://youtu.be/rrk0dRZUqbY"> TODO - View Demo</a>
    ·
    <a href="https://github.com/vatsalunadkat/Ergonomic-Radial-Inclusive-Chorded-Keyboard/issues">Report Bug</a>
    ·
    <a href="https://github.com/vatsalunadkat/Ergonomic-Radial-Inclusive-Chorded-Keyboard/issues">Request Feature</a>
  </p>
</div>


<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
        <li><a href="#features">Features</a></li>
        <li><a href="#future-scope">Future Scope</a></li>
      </ul>
    </li>
    <li>
      <a href="#project-artifacts">Project Artifacts</a>
      <ul>
        <li><a href="#swipe-typing">Swipe Typing</a></li>
        <li><a href="#typing-with-controller">Typing with Controller</a></li>
        <li><a href="#keyboard-typing-with-no-fingers-vs-typing-with-controller">Keyboard Typing with No Fingers vs Typing with Controller</a></li>
        <li><a href="#todo-architecture-diagram">TODO Architecture Diagram</a></li>
        <li><a href="#controller-input-data-calculations-todo">Controller INPUT Data Calculations TODO</a></li>
      </ul>
    </li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project
<div align="center">
  <img src="" height="400" />
</div>

A type of ergonomic keyboard that will take input from either 2 virtual joysticks on their device screen or 2 physical joysticks of a pre-existing controller/gamepad.

The input is provided as a combination of gestures, i.e. swiping/flicking up on both joysticks will type the letter "A" (also known as a chord input). It requires the same amount of effort to type any letter, number or symbol. Removing the dependency of multiple small keys will make it accessible for people with physical disabilities. Also, the letters and numbers are positioned in a logical order making it easier for people with mental disabilities (especially autism) to use. We focused on the design characteristics and the chord coding to improve typing efficiency.

The first version released supports a simple notepad application taking in input. Further updates will support other apps (Such as WhatsApp and EverNote) and controlling the general OS GUI with the controller (Specific type of controller).

Will have different modes. The simple mode will only take input from the 2 analogue sticks. Furthur modes will take in input using multiple combinations such as the left and right trigger.

The simple mode will target disabled users who have difficulty with moving their fingers. The simple mode would only require 2 joysticks to operate and would not use other buttons and triggers.

The complex mode will target the users who want to type faster without the use of the on-screen keyboard. The complex mode would have the use of the basic 2 analog sticks plus the triggers at the back of the controller and other buttons.

Some examples of real-life use cases:
- Better typing for people with disability.
- Typing on gaming consoles and TV screens.
- Taking notes in class without looking.

Input is provided through combinations of swipe inputs or joystick movements (chord input). For example, moving the left stick right then the right stick up types "5". This approach:
- Requires equal effort for any character
- Removes dependency on fine motor skills for small keys
- Uses logical positioning for easier memorization

### Built With

* [![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)](#)
* [![Kotlin](https://img.shields.io/badge/Kotlin-%237F52FF.svg?logo=kotlin&logoColor=white)](#)
* Deployed on [![Google Play Store](https://img.shields.io/badge/Google_Play-414141?logo=google-play&logoColor=white)](#)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->

### Features
- [x] Ability to provide input using swipe
- [x] Introductory Game to learn typing

### Future scope
- [ ] Enhanced Exports - More customizations (FHIR compatible) for the export feature.
- [ ] Enhanced Visualization - Adding more interactive and customizable visualization options.
- [ ] Google SSO Login - for quick and easy access to import the data.
- [ ] Enhanced compatibility - Add support to directly import data from the Fitbit APIs, as well as integration and support with Apple Health and Garmin.
- [ ] Integration with Wearable Devices - Integrating with wearable devices to directly import sleep data, providing real-time insights and personalized recommendations.

See the [open issues](https://github.com/vatsalunadkat/Ergonomic-Radial-Inclusive-Chorded-Keyboard/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- OTHER ARTIFACTS -->
## Project Artifacts

### Swipe Typing
<img src="https://github.com/vatsalunadkat/Ergonomic-Radial-Inclusive-Chorded-Keyboard/blob/main/documentation/swipe.gif" height="400" />

### Typing with Controller
<img src="https://github.com/vatsalunadkat/Ergonomic-Radial-Inclusive-Chorded-Keyboard/blob/main/documentation/controller.gif" height="400" />

### Keyboard Typing with No Fingers vs Typing with Controller
<img src="https://github.com/vatsalunadkat/Ergonomic-Radial-Inclusive-Chorded-Keyboard/blob/main/documentation/no%20hands.gif" height="400" /> vs <img src="https://github.com/vatsalunadkat/Ergonomic-Radial-Inclusive-Chorded-Keyboard/blob/main/documentation/no%20hands%20type.gif" height="400" />

### TODO Architecture Diagram
<img src="https://github.com/vatsalunadkat/sleep-pattern-analysis/blob/e9c002705fff6561a0f68450b7da10759fb7592b/documentation/Images/architecture_diagram.png" height="300" />

### Controller INPUT Data Calculations TODO
TODO


<!-- CONTACT -->
<!-- ## Contact

Vatsal Paresh Unadkat
Project Link: [https://github.com/vatsalunadkat/Ergonomic-Radial-Inclusive-Chorded-Keyboard](https://github.com/vatsalunadkat/Ergonomic-Radial-Inclusive-Chorded-Keyboard)

-->

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- MARKDOWN LINKS -->
[React.js]: https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB
[React-url]: https://reactjs.org/
