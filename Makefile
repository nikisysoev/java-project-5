build:
	./gradlew clean build

test:
	./gradlew test

clean:
	./gradlew clean

install:
	./gradlew installDist

start-dist:
	./build/install/app/bin/app

lint:
	./gradlew checkstyleMain checkstyleTest

report:
	./gradlew jacocoTestReport

check-updates:
	./gradlew dependencyUpdates

start:
	./gradlew bootRun --args='--spring.profiles.active=dev'

start-prod:
	./gradlew bootRun --args='--spring.profiles.active=prod'

generate-migrations:
	gradle diffChangeLog