# Copilot Tracker Monitor - Fine-Tuned Implementation

## Overview
This is a comprehensive refactoring and fine-tuning of the Copilot Tracker Monitor application. The project has been significantly improved with better architecture, logging, configuration management, and code organization.

## Key Improvements

### 1. **Modular Architecture**
- **Before**: Monolithic single-file design with ~1,100 lines
- **After**: Separated into multiple focused classes organized by responsibility

**New Package Structure**:
```
com.cognizant.copilot/
├── config/
│   └── AppConfig.java              # Configuration management
├── model/
│   ├── UserInfo.java               # User data model
│   ├── UsageEntry.java             # Usage entry data model
│   ├── TeamMetric.java             # Team metrics data model
│   ├── ResourceMetric.java         # Resource metrics data model
│   └── TrackerResult.java          # Analysis result container
├── service/
│   ├── UserReadService.java        # User data reading
│   ├── UsageReadService.java       # Usage data reading
│   ├── TrackerAnalysisService.java # Core analysis logic
│   ├── ReportGenerationService.java # Report & chart generation
│   └── EmailService.java           # Email communication
├── util/
│   ├── StringUtils.java            # String utilities
│   ├── ExcelUtils.java             # Excel parsing utilities
│   ├── DateFormatUtils.java        # Date formatting
│   └── NameMappingUtils.java       # Name normalization
└── CopilotTrackerMonitor.java      # Main orchestrator (refactored)
```

### 2. **Logging Framework**
- **Added**: SLF4J + Logback for professional logging
- **Configured**: Rolling file appenders with size and time-based rotation
- **Benefits**: 
  - Tracks application execution in detail
  - Persists logs to `logs/` directory
  - Automatic log rotation to manage disk space
  - Configurable log levels

### 3. **Externalized Configuration**
- **Created**: `application.properties` for environment-specific settings
- **Managed Settings**:
  - Excel file path
  - Sheet names
  - Email configuration
  - SMTP settings
  - Tracker parameters
  - Date formats
  - Chart configuration

### 4. **Improved Code Organization**

#### Service Layer
- **UserReadService**: Reads user information from Excel
- **UsageReadService**: Reads usage metrics from Excel
- **TrackerAnalysisService**: Core analytics and data processing
- **ReportGenerationService**: Handles all reporting and chart generation
- **EmailService**: Manages email reminders and escalations

#### Utility Classes
- **StringUtils**: Common string operations (normalize, clean, validate)
- **ExcelUtils**: Excel cell value extraction with type handling
- **DateFormatUtils**: Centralized date formatting
- **NameMappingUtils**: Name normalization with extensible mapping

#### Data Models
- Clean POJO classes with proper encapsulation
- `toString()` methods for better logging
- Semantic organization of related fields

### 5. **Better Error Handling**
- Improved exception logging with stack traces
- Graceful error recovery
- Meaningful error messages
- Proper resource management with try-with-resources

### 6. **Code Quality Improvements**

| Aspect | Before | After |
|--------|--------|-------|
| **Lines of Code** | 1,100+ | ~150 (main) |
| **Cyclomatic Complexity** | High | Low |
| **Reusability** | Low | High |
| **Testability** | Difficult | Easy |
| **Maintainability** | Complex | Clean |
| **Documentation** | Minimal | Comprehensive |
| **Logging** | System.out | SLF4J/Logback |
| **Configuration** | Hardcoded | Externalized |

### 7. **Configuration Files Added**

#### `application.properties`
```properties
excel.file.path=C:\\Users\\671545\\Downloads\\GenAI -Tracker.xlsx
email.enabled=false
email.smtp.host=smtp.office365.com
tracker.continuous.missing.days=5
```

#### `logback.xml`
- Console appender for real-time feedback
- File appender with rolling policies
- Debug logging for the application package
- Info logging for external libraries

### 8. **Enhanced Dependency Management**
**Added to pom.xml**:
- `org.slf4j:slf4j-api:2.0.9` - Logging facade
- `ch.qos.logback:logback-classic:1.4.11` - Logging implementation

### 9. **Improved Main Method**
- Better structured orchestration
- Clear separation of concerns
- Enhanced error handling
- Logging of major milestones

## File Structure

```
Copilot_Tracker/
├── pom.xml
├── README.md (this file)
├── src/
│   ├── main/
│   │   ├── java/com/cognizant/copilot/
│   │   │   ├── config/
│   │   │   ├── model/
│   │   │   ├── service/
│   │   │   ├── util/
│   │   │   └── CopilotTrackerMonitor.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── logback.xml
│   └── target/
└── dashboard-charts/
```

## Usage

### Default (Last 5 Days)
```bash
mvn clean compile exec:java
```

### Custom Date Range
```bash
mvn clean compile exec:java -Dexec.args="08/05/2026 08/11/2026"
```

## Configuration

### Excel File Path
Update `application.properties`:
```properties
excel.file.path=your/path/to/GenAI-Tracker.xlsx
```

### Email Configuration
```properties
email.enabled=true
email.smtp.host=smtp.office365.com
email.smtp.port=587
email.from.address=your-email@cognizant.com
email.from.password=your-app-password
```

### Logging
Adjust log levels in `logback.xml`:
```xml
<root level="INFO">
<logger name="com.cognizant.copilot" level="DEBUG"/>
```

## Design Patterns Applied

1. **Singleton Pattern**: AppConfig
2. **Service Layer Pattern**: All service classes
3. **Utility Class Pattern**: StringUtils, ExcelUtils, etc.
4. **Data Model Pattern**: Clean POJOs
5. **Dependency Injection**: Constructor-based in services
6. **Factory Pattern**: MetricKey factory methods

## Benefits of This Refactoring

✅ **Maintainability**: Each class has a single responsibility  
✅ **Reusability**: Services can be used independently  
✅ **Testability**: Classes can be unit tested in isolation  
✅ **Scalability**: Easy to add new features  
✅ **Observability**: Comprehensive logging throughout  
✅ **Configuration**: Environment-specific settings management  
✅ **Documentation**: Self-documenting code with Javadoc  
✅ **Performance**: No performance degradation  

## Future Enhancements

1. Add Unit Tests (JUnit 5 + Mockito)
2. Add Integration Tests
3. Create REST API wrapper
4. Add Spring Boot framework
5. Implement database persistence
6. Add batch processing capabilities
7. Create web dashboard UI
8. Add data validation framework
9. Implement audit logging
10. Add metrics and monitoring

## Building and Running

### Build
```bash
cd Copilot_Tracker
mvn clean package
```

### Run Generated JAR
```bash
java -jar target/CopilotTrackerMonitor.jar
```

### Run with Custom Date Range
```bash
java -jar target/CopilotTrackerMonitor.jar 08/05/2026 08/11/2026
```

## Logs
Check logs in:
```
./logs/copilot-tracker.log
```

## Notes
- Email sending is disabled by default (set `email.enabled=false`)
- Change to `true` only after SMTP credentials are verified
- Charts are generated in `dashboard-charts/` directory
- All dates follow MM/dd/yyyy format
- Configuration is loaded at application startup

## Version
- **Current**: 1.0-SNAPSHOT (Fine-tuned)
- **Java**: 17+
- **Maven**: 3.6+

---
*Last Updated: 2026*  
*Maintained by: Cognizant GenAI Team*
