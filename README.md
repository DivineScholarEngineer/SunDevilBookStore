# SunDevilBookStore
This is a CSE360 project of use making a bookstore for ASU where we have 3 section: Buyers, Sellers, Admin

# Installation and Running the Maven Project
1. Install Maven Plugin (If not already installed)
- Open Eclipse.
- Go to `Help` > `Eclipse Marketplace`.
- In the search box, type "Maven" and install the "Maven Integration for Eclipse" plugin.
- Restart Eclipse after installation.
2. Import the Maven Project:
   - Clone the repository from GitHub using the repository URL.
     - Open Eclipse, go to 'File' > 'Import' > 'Git' > 'Projects from Git' > 'Clone URI'.
     - Paste the repository URL and follow the prompts to clone the project.
   - Once cloned, go to File > Import > Maven > Existing Maven Projects.
   - Select the cloned repository folder and import it into Eclipse.
3. Manage Dependencies in pom.xml:
- If additional dependencies are needed:
- Open the pom.xml file located in the root of the project.
- Check for existing dependencies and add any additional ones required for the project, such as logging frameworks, database connectors, or JavaFX dependencies.
  - <dependencies>
        <!-- JavaFX Dependencies -->
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-base</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-graphics</artifactId>
            <version>${javafx.version}</version>
        </dependency>

        <!-- MySQL JDBC Connector -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.connector.version}</version>
        </dependency>

        <!-- Apache PDFBox (For PDF handling) -->
        <dependency>
            <groupId>org.apache.pdfbox</groupId>
            <artifactId>pdfbox</artifactId>
            <version>${pdfbox.version}</version>
        </dependency>

        <!-- Apache POI (For Excel handling) -->
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi</artifactId>
            <version>${poi.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>${poi.version}</version>
        </dependency>

        <!-- Logging Dependency (Optional but Recommended) -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.36</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.7.36</version>
        </dependency>

        <!-- Additional Dependencies if needed -->
        <!-- Add any additional libraries or dependencies here -->
    </dependencies>
- Save the pom.xml file, and Maven will automatically download the specified dependencies.
4. Update Database Credentials:
- If the project connects to a database, ensure the configuration file is updated with the correct database credentials.
- These credentials can be found in MySQL Workbench or your database management system.
5. Run the Project:
  - Open the project in Eclipse and navigate to SplashScreen.java, the main entry point for the application.
  - Right-click the project in the Project Explorer and select Run As > Maven build....
  - In the "Goals" field, enter javafx:run and click Run to execute the application.
6. Update Database Credentials:
- If the project connects to a database, ensure the configuration file is updated with the correct database credentials.
- These credentials can be found in MySQL Workbench or your database management system.
7. Build and Run Maven Project
- Right-click on your Maven project in the Project Explorer.
- Go to `Run As` > `Maven build...`.
- In the "Goals" field, enter `javafx:run` to build your project.
- Click `Run`. Maven will compile and package your project, downloading any missing dependencies.
- Examples of common goals:
  - clean: Deletes the target directory.
  - compile: Compiles the source code.
  - test: Runs tests for the project.

