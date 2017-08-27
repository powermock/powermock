#!/bin/sh
read -p "Version to generate javadoc for: " version
project_names=(powermock-core powermock-api-easymock powermock-api-mockito powermock-api-mockito2 powermock-api-mockito-common\
 powermock-reflect powermock-api-support powermock-module-junit4-common powermock-module-junit4 powermock-module-junit4-rule\
 powermock-classloading-module powermock-classloading-xstream powermock-module-javaagent powermock-module-junit4-rule-agent\
 powermock-module-junit3 powermock-module-junit4-legacy powermock-module-testng-common powermock-module-testng\
 powermock-module-testng-agent powermock-modules-impl powermock-classloading-objenesis)

echo "Generating Javadoc for version ${version}."

for project_name in ${project_names[*]}
do
	echo "Generating for ${project_name}"
    curl -Ss http://www.javadoc.io/doc/org.powermock/${project_name}/${version} >/dev/null 2>&1
done
echo "Completed"

