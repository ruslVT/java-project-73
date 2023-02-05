clean:
	./gradlew clean

build:
	./gradlew clean build

install:
	./gradlew installDist

start:
	./gradlew run

start-dist:
	./build/install/app/bin/app

lint:
	./gradlew checkstyleMain checkstyleTest

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

check-updates:
	./gradlew dependencyUpdates

