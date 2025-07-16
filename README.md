# Deadlock Detection Project

System for simulation and detection of deadlocks in Operating Systems environments, developed as part of an undergraduate university course (Sistemas Operacionais discipline).

## Table of Contents
- [Description](#description)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Technologies Used](#technologies-used)
- [Team](#team)
- [License](#license)

## Description
This project aims to simulate resource and process management in an operating system, allowing the analysis and detection of deadlock situations. The graphical interface makes it easy to configure resources, processes, and visualize the system state.

## Features
- Resource and process registration
- Resource allocation and release
- Process elimination
- Automatic deadlock detection
- Visualization of resource and process states
- Intuitive graphical interface

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/HielSaraiva/project-deadlock-detection.git
   ```
2. Access the project folder:
   ```bash
   cd project-deadlock-detection
   ```
3. Compile the project using Maven:
   ```bash
   ./mvnw clean install
   ```

## Usage
1. Run the application:
   ```bash
   ./mvnw javafx:run
   ```
2. Use the graphical interface to register resources, processes, and simulate the operation of the operating system.

## Technologies Used
- Java 17
- JavaFX 20
- Maven 3.9+

## Team
- Hiel Saraiva
- Roberta Alanis
- Charles Lima

## License
This project is licensed under the MIT License. See the LICENSE file for more details.
