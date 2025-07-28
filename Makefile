# MAKEFILE
#
# @author      Maxim
# -----------------------------------------------------------------------------------

PROJECT=anymapper

# --------------------------------CONFIG-----------------------------------------------
#INCREMENT_TYPE

# -----------------------------------CMD-----------------------------------------------
GRADLE=./gradlew
GIT=git

# ---------------------------------VERSION---------------------------------------------
# container release version
VERSION=$(shell cat VERSION)
NEW_VERSION=$(shell ./resources/scripts/version_increment.sh ${VERSION} ${INCREMENT_TYPE})

# container release build
RELEASE=$(shell cat RELEASE)
ifeq ($(RELEASE),0)
	RELEASE="rc"
endif

# Display general help about this command
.PHONY: help
help:
	@echo ""
	@echo "Payment-Service Makefile."
	@echo "The following commands are available:"
	@echo ""
	@echo "    make clean                               : Remove any build artifact"
	@echo "    make build                               : Build PRODUCTION docker files"
	@echo ""

.PHONY: clean build releaseConfigDryRun releaseMavenCentral releaseVersionIncrement
clean:
	$(GRADLE) clean

# application build
.PHONY: build
build:
	$(GRADLE) build

releaseVersionIncrement: build
	if [ -z ${INCREMENT_TYPE} ]; then echo "FATAL: ENV INCREMENT_TYPE must be defined."; exit 2; fi
	if [ -z ${VERSION} ]; then echo "FATAL: ENV VERSION must be defined."; exit 2; fi
	if [ -z ${NEW_VERSION} ]; then echo "FATAL: ENV NEW_VERSION must be defined."; exit 2; fi

	mkdir -p ./target/deploy && \
	echo "Version: ${VERSION}" && \
	echo "New Version: ${NEW_VERSION}" && \
	echo "Incrementing version '${VERSION}' to '${NEW_VERSION}'" && \
	echo "${NEW_VERSION}" > VERSION && \
	$(GRADLE) clean build publishToMavenLocal jreleaserRelease -PpreviousTag=${VERSION} && \
	$(GIT) commit -am"[${INCREMENT_TYPE}] increment and release version [${NEW_VERSION}]" && \
	$(GIT) push origin master

releaseConfigDryRun: clean
	$(GRADLE) jreleaserConfig

releaseMavenCentral: releaseVersionIncrement
	rm -rf ./target/deploy
	$(GRADLE) jreleaserDeploy

