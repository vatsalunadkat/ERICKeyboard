<a name="readme-top"></a>

<!-- PROJECT LOGO -->
<div align="center">
  <h3 align="center">Ergonomic Inclusive Controller Keyboard (EICK)</h3>

  <p align="center">
    An ergonomic keyboard system for Android using joystick-based chord input
    <br />
    <br />
    <a href="https://github.com/vatsalunadkat/ERICKeyboard/issues">Report Bug</a>
    ·
    <a href="https://github.com/vatsalunadkat/ERICKeyboard/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about-the-project">About The Project</a></li>
    <li><a href="#built-with">Built With</a></li>
    <li><a href="#project-structure">Project Structure</a></li>
    <li><a href="#use-cases">Use Cases</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

Input is provided through combinations of joystick movements (chord input). For example, moving the left stick right then the right stick up types "5". This approach:
- Requires equal effort for any character
- Removes dependency on fine motor skills for small keys
- Uses logical positioning for easier memorization

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

* <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" title="Android" height="20" />
* <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" title="Java" height="20" />
* <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle" title="Gradle" height="20" />

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- PROJECT STRUCTURE -->
## Project Structure

### Android-Controller
A standalone app that accepts input from physical game controllers:
- State-machine based chord input (HOME → LEFT/RIGHT states)
- Supports numbers 0-9, space, newline, and backspace
- Visual state diagram display
- Built-in text editor for testing

### Android-OnScreen
An Android Input Method Service (IME) providing an on-screen number pad keyboard.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USE CASES -->
## Use Cases

- Accessible typing for users with motor disabilities
- Text input on gaming consoles and TV interfaces
- Eyes-free note taking

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

### Requirements
- Android device
- For Android-Controller: Compatible Bluetooth/USB game controller

### Building
Open either project folder in Android Studio and build using Gradle.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Vatsal Unadkat - [GitHub](https://github.com/vatsalunadkat)

Project Link: [https://github.com/vatsalunadkat/ERICKeyboard](https://github.com/vatsalunadkat/ERICKeyboard)

<p align="right">(<a href="#readme-top">back to top</a>)</p>
