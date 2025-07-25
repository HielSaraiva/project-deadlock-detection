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

## Running the App from the Distribution Zip

After building the project, a distribution zip file is generated using the following command:
```bash
./mvnw javafx:jlink
```
This will create a zip file at `target/app.zip`.

To run the application from this distribution in 'dist/app.zip', follow these steps:

1. Unzip the file:
   ```bash
   unzip dist/app.zip -d dist/
   ```
2. The executable file will be located at `dist/app/bin/app`.
3. Run the application:
   ```bash
   dist/app/bin/app
   ```

## Technologies Used
- Java 17
- JavaFX 20
- Maven 3.9+

## Team
- Hiel Saraiva Freitas de Queiroga
- Roberta Alanis Andrade Nogueira
- Charles Lima de Brito Filho

## Screens

### Initial screen

<img src="/assets/img/Inicial screen.png" alt="Initial screen" style="width: 600px">

### Running

<video src="https://github.com/user-attachments/assets/448cdd6b-3169-4677-bf75-181e6f465f8b"><\video>

## License
This project is licensed under the MIT License. See the LICENSE file for more details.
