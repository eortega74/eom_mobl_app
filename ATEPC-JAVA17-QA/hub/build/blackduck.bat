SET BLACKDUCK_URL=%ATE_JAVA17_BLACKDUCK_URL%
SET BLACKDUCK_API_TOKEN=%ATE_JAVA17_BLACKDUCK_API_TOKEN%
SET DETECT_PROJECT_NAME=%ATE_JAVA17_DETECT_PROJECT_NAME%
SET DETECT_PROJECT_VERSION=%ATE_JAVA17_DETECT_PROJECT_VERSION%



IF "%BLACKDUCK_URL%"=="" (
	ECHO BLack Duck Server URL is NOT defined, please set the BLACKDUCK_URL environment variable
	Exit /B 2
)

IF "%BLACKDUCK_API_TOKEN%"=="" (
	ECHO BLack Duck API Token NOT defined, please set the BLACKDUCK_API_TOKEN environment variable
	Exit /B 2
)

IF "%DETECT_PROJECT_NAME%"=="" (
	ECHO BLack Duck Project Name NOT defined, please set the DETECT_PROJECT_NAME environment variable
	Exit /B 2
)

IF "%DETECT_PROJECT_VERSION%"=="" (
	ECHO BLack Duck Project Name NOT defined, please set the DETECT_PROJECT_VERSION environment variable
	Exit /B 2
)

IF "%JAVA_HOME%"=="" (
	ECHO JAVA_HOME Name NOT defined, please set the JAVA_HOME environment variable
	Exit /B 2
)

java -jar C:\apps\blackduck\synopsys-detect-8.8.0.jar %*
