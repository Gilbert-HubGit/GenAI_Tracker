# Contributing Guide - Copilot Tracker Monitor

## Project Overview

The Copilot Tracker Monitor is a Java-based application that analyzes GenAI Copilot usage metrics from Excel files and generates reports with visualizations.

## Project Structure

```
src/main/java/com/cognizant/copilot/
├── CopilotTrackerMonitor.java       # Main entry point (orchestrator)
├── config/
│   └── AppConfig.java               # Configuration management (singleton)
├── model/
│   ├── UserInfo.java                # User information model
│   ├── UsageEntry.java              # Usage metrics model
│   ├── TeamMetric.java              # Team-wise aggregated metrics
│   ├── ResourceMetric.java          # Resource-wise aggregated metrics
│   └── TrackerResult.java           # Analysis result container
├── service/
│   ├── UserReadService.java         # Read users from Excel
│   ├── UsageReadService.java        # Read usage metrics from Excel
│   ├── TrackerAnalysisService.java  # Analyze and categorize users
│   ├── ReportGenerationService.java # Generate reports and charts
│   └── EmailService.java            # Send reminder and escalation emails
└── util/
    ├── StringUtils.java             # String manipulation utilities
    ├── ExcelUtils.java              # Excel cell parsing utilities
    ├── DateFormatUtils.java         # Date formatting constants
    └── NameMappingUtils.java        # Name normalization and mapping
```

## Development Workflow

### 1. Setting Up Development Environment

```bash
# Clone repository
git clone <repo-url>
cd Copilot_Tracker

# Build the project
mvn clean compile

# Run tests (if added)
mvn test

# Package the application
mvn clean package
```

### 2. Running the Application

**Default (last 5 days)**:
```bash
mvn clean compile exec:java
```

**Custom date range**:
```bash
mvn clean compile exec:java -Dexec.args="08/05/2026 08/11/2026"
```

**From built JAR**:
```bash
java -jar target/CopilotTrackerMonitor.jar
java -jar target/CopilotTrackerMonitor.jar 08/05/2026 08/11/2026
```

### 3. Configuration

Before running, update `src/main/resources/application.properties`:

```properties
# Required: Set your Excel file path
excel.file.path=C:\\path\\to\\GenAI-Tracker.xlsx

# Optional: Configure email settings
email.smtp.host=smtp.office365.com
email.smtp.port=587
email.from.address=your-email@cognizant.com
email.from.password=your-app-password
```

## Code Conventions

### Naming Conventions
- **Classes**: PascalCase (e.g., `UserReadService`)
- **Methods**: camelCase (e.g., `readUserList()`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `CONTINUOUS_MISSING_DAYS`)
- **Variables**: camelCase (e.g., `excelPath`)

### Class Organization
```java
public class ExampleService {
    // 1. Static fields/constants
    private static final Logger logger = LoggerFactory.getLogger(ExampleService.class);
    
    // 2. Instance fields
    private final String configValue;
    
    // 3. Constructors
    public ExampleService() {
        this.configValue = AppConfig.getInstance().getString("key");
    }
    
    // 4. Public methods
    public void publicMethod() {
        // implementation
    }
    
    // 5. Private methods
    private void privateMethod() {
        // implementation
    }
}
```

### Logging Best Practices
```java
// Use SLF4J through logger
logger.info("User list read successfully");
logger.debug("Detailed debugging information");
logger.error("Error occurred", exception);
logger.warn("Warning message");

// Don't use System.out for logs
// System.out.println("This is bad"); // ❌
```

### Exception Handling
```java
// Good: Catch specific exceptions
try {
    // code
} catch (FileNotFoundException e) {
    logger.error("Excel file not found: {}", excelPath, e);
    throw new RuntimeException("Failed to read Excel", e);
}

// Avoid: Catching all exceptions
// try { } catch (Exception e) { } // ❌
```

### String Utilities
```java
// Use StringUtils for common operations
if (StringUtils.isBlank(value)) { }
String normalized = StringUtils.normalize(name);
String email = StringUtils.generateDummyEmail(name);

// Don't create your own
// if (value == null || value.isEmpty()) { } // ❌
```

## Adding New Features

### Adding a New Report Type

1. **Add method to `ReportGenerationService`**:
```java
public void printCustomReport(TrackerResult result) {
    logger.debug("Generating custom report");
    // Implementation
    System.out.println("Custom Report:");
    // ...
}
```

2. **Call from main method**:
```java
reportService.printCustomReport(result);
```

### Adding a New Service

1. **Create service class** in `com.cognizant.copilot.service`:
```java
public class NewFeatureService {
    private static final Logger logger = LoggerFactory.getLogger(NewFeatureService.class);
    
    public void executeFeature() {
        logger.info("Executing new feature");
        // Implementation
    }
}
```

2. **Inject in main method**:
```java
NewFeatureService featureService = new NewFeatureService();
featureService.executeFeature();
```

### Adding Configuration Parameters

1. **Add to `application.properties`**:
```properties
feature.parameter1=value1
feature.parameter2=value2
```

2. **Retrieve in code**:
```java
String value = AppConfig.getInstance().getString("feature.parameter1");
int numValue = AppConfig.getInstance().getInt("feature.parameter2", 10);
```

## Testing Approach

### Unit Testing Example
```java
@Test
public void testNameNormalization() {
    String input = "P, Gilbert Roy";
    String expected = "pgilbertroy";
    String actual = NameMappingUtils.applyNameMapping(input);
    assertEquals(expected, actual);
}
```

### Integration Testing Example
```java
@Test
public void testReadAndAnalyzeFlow() {
    UserReadService userService = new UserReadService();
    List<UserInfo> users = userService.readUserList();
    assertNotNull(users);
    assertTrue(users.size() > 0);
}
```

## Performance Considerations

1. **Excel Reading**: Uses try-with-resources for efficient resource management
2. **Stream Operations**: Leverage Java streams for efficient data processing
3. **Logging**: Use appropriate log levels to avoid excessive output
4. **Configuration**: Singleton pattern for AppConfig to load once

## Security Best Practices

1. **Sensitive Data**: Never hardcode SMTP passwords
2. **Configuration Files**: Use property files for credentials
3. **Logging**: Don't log sensitive information
4. **Input Validation**: Validate Excel file paths and data

## Common Issues and Solutions

### Issue: Excel file not found
**Solution**: Check `application.properties` - verify `excel.file.path` is correct

### Issue: Email not sending
**Solution**: 
- Verify `email.enabled=true` in properties
- Check SMTP credentials
- Ensure app-specific password for Office 365

### Issue: Date parsing errors
**Solution**: Verify Excel date format matches `date.input.format` in properties

### Issue: Charts not generating
**Solution**: Check `chart.output.directory` is writable and exists

## Building and Deploying

### Build for Production
```bash
mvn clean package -DskipTests
```

### Create Distribution Package
```bash
mvn clean package assembly:single
```

### Deploy JAR
```bash
java -jar CopilotTrackerMonitor.jar 08/01/2026 08/31/2026
```

## Documentation Guidelines

### JavaDoc Comments
```java
/**
 * Reads user information from Excel file.
 * 
 * @return List of users read from the Excel file
 * @throws Exception if Excel file cannot be read
 */
public List<UserInfo> readUserList() throws Exception {
    // Implementation
}
```

### Inline Comments
```java
// Use for complex logic only, not for obvious code
// Good:
// Filter entries to date range and group by user
List<UsageEntry> filtered = entries.stream()
    .filter(e -> isWithinRange(e.getDate()))
    .collect(Collectors.groupingBy(e -> e.getUserId()));

// Not needed:
// String normalized = normalize(value); // Obviously normalizes
```

## Submitting Changes

1. **Create a feature branch**:
```bash
git checkout -b feature/my-feature
```

2. **Make changes** following conventions

3. **Test thoroughly**:
```bash
mvn clean test
```

4. **Commit with clear messages**:
```bash
git commit -m "Add: New report generation feature"
```

5. **Push and create pull request**

## Code Review Checklist

- ✅ Follows naming conventions
- ✅ Proper error handling and logging
- ✅ No hardcoded values (use config)
- ✅ Efficient implementation
- ✅ Clear and documented
- ✅ Tested (if applicable)
- ✅ No breaking changes
- ✅ Performance acceptable

## Resources

- [SLF4J Documentation](https://www.slf4j.org/)
- [Logback Configuration](https://logback.qos.ch/manual/configuration.html)
- [Apache POI Guide](https://poi.apache.org/components/spreadsheet/index.html)
- [JFreeChart Examples](https://www.jfree.org/jfreechart/)
- [Java Streams Tutorial](https://docs.oracle.com/javase/tutorial/collections/streams/)

## Questions or Issues?

Contact: Cognizant GenAI Team

---
*Last Updated: 2026*  
*Version: 1.0-FT*
