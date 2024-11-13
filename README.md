# SunDevilBookStore
This is a CSE360 project of use making a bookstore for ASU where we have 3 section: Buyers, Sellers, Admin

# Installation and Running the Maven Project
1. Install Maven Plugin (If not already installed)
- Open Eclipse.
- Go to `Help` > `Eclipse Marketplace`.
- In the search box, type "Maven" and install the "Maven Integration for Eclipse" plugin.
- Restart Eclipse after installation.

2. Create a Maven Project
- Go to `File` > `New` > `Project`.
- In the wizard, select `Maven Project` under the `Maven` folder, then click `Next`.
- Choose `Create a simple project (skip archetype selection)` if you want a basic setup, then click `Next`.
- Enter the `Group Id` and `Artifact Id` (basic identifiers for your project).
- Click `Finish` to create the project.

3. Add javafx to Maven Dependencies if not included
- Open the `pom.xml` file in your project (found in the root of your Maven project).
- In the `<dependencies>` section, add any dependencies you need:
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
- Save the `pom.xml` file, and Maven will download the specified dependencies.

4. Build and Run Maven Project
- Right-click on your Maven project in the Project Explorer.
- Go to `Run As` > `Maven build...`.
- In the "Goals" field, enter `javafx:run` to build your project.
- Click `Run`. Maven will compile and package your project, downloading any missing dependencies.

5. Run Specific Maven Goals
- To run specific goals, like `mvn clean`, `mvn compile`, etc., right-click on your project, choose `Run As`, then select the Maven command.
- For example:
- `clean`: Deletes the `target` directory.
- `compile`: Compiles the source code.
- `test`: Runs the tests.

6. Check Maven Repositories and Dependencies
- Open the `pom.xml` file and go to the `Dependencies` tab (available in Eclipse's Maven editor view).
- Here, you can manage dependencies, repositories, and plugins for your project
