# Dependency Management Improvements

## Summary
Refactored the Maven multi-module project to follow best practices for dependency management, eliminating duplication and centralizing version control.

## Changes Made

### 1. Parent POM (`pom.xml`)
**Added:**
- ✅ Centralized all dependency versions in properties section
- ✅ Added `pluginManagement` section to centralize plugin versions
- ✅ Created properties for all version numbers:
  - `jackson.version`: 2.19.2
  - `picocli.version`: 4.7.7
  - `junit.version`: 5.13.4
  - `log4j.version`: 2.25.1
  - `javafx.version`: 21.0.7 (standardized from 21.0.5)
  - `testfx.version`: 4.0.16-alpha
  - Plugin versions for compiler, surefire, shade, javafx-maven, and native-maven plugins

**Improved:**
- ✅ Changed `teammaker-core` dependency version from hardcoded `1.0-SNAPSHOT` to `${project.version}`
- ✅ All dependency versions now reference properties instead of hardcoded values

### 2. Core Module (`core/pom.xml`)
**Removed:**
- ❌ Duplicate `<properties>` section (inherited from parent)
- ❌ All version tags from dependencies (managed by parent)
- ❌ Version and configuration from plugin declarations (managed by parent)
- ❌ `native.maven.plugin.version` property (moved to parent)

**Result:** ~40 lines removed, cleaner and more maintainable

### 3. CLI Module (`cli/pom.xml`)
**Removed:**
- ❌ Duplicate `<properties>` section (inherited from parent)
- ❌ All version tags from dependencies (managed by parent)
- ❌ Version and configuration from plugin declarations (managed by parent)
- ❌ `native.maven.plugin.version` property (moved to parent)

**Result:** ~40 lines removed, cleaner and more maintainable

### 4. GUI Module (`gui/pom.xml`)
**Removed:**
- ❌ Duplicate `<properties>` section (inherited from parent)
- ❌ All version tags from dependencies (managed by parent)
- ❌ Version and configuration from plugin declarations (managed by parent)
- ❌ Local JavaFX version override (now uses parent's standardized version)

**Fixed:**
- ✅ JavaFX version upgraded from 21.0.5 to 21.0.7 (standardized with parent)

**Result:** ~30 lines removed, version consistency achieved

## Benefits

### 1. **Single Source of Truth**
All versions are now defined in one place (parent POM), making updates much easier and less error-prone.

### 2. **Version Consistency**
- Eliminated version mismatches (e.g., JavaFX 21.0.5 vs 21.0.7)
- Standardized plugin versions across all modules
- Maven compiler plugin now consistently 3.14.1 (was 3.14.0 in core/cli)
- Maven surefire plugin now consistently 3.5.4 (was 3.5.3 in core/cli)

### 3. **Reduced Duplication**
- ~110 lines of duplicated code removed across child modules
- Properties section eliminated from all child POMs
- No version numbers in child POMs (except where needed for module-specific customization)

### 4. **Easier Maintenance**
- To upgrade a dependency, change only one property in the parent POM
- Plugin configuration defined once, inherited by all modules
- Reduced chance of inconsistencies when adding new modules

### 5. **Better Maven Best Practices**
- Proper use of `<dependencyManagement>` for dependency versions
- Proper use of `<pluginManagement>` for plugin versions
- Child modules declare what they need, not how they configure it

## Verification

Build completed successfully with all tests passing:
```
[INFO] BUILD SUCCESS
[INFO] Total time:  19.964 s
Tests run: 71, Failures: 0, Errors: 0, Skipped: 0
```

## Future Recommendations

1. **Consider using Maven BOM (Bill of Materials)** for frameworks with multiple artifacts (e.g., JavaFX, Jackson)
2. **Add version property comments** indicating why specific versions are chosen
3. **Document upgrade policy** in README for when to update dependency versions
4. **Consider using Maven Versions Plugin** for automated dependency updates
5. **Add dependency convergence enforcement** via enforcer plugin
