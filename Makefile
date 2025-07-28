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
build: clean
	$(GRADLE) build

releaseVersionIncrement: build
	if [ -z ${INCREMENT_TYPE} ]; then echo "FATAL: ENV INCREMENT_TYPE must be defined."; exit 2; fi

	@new_version=$(shell ./resources/scripts/version_increment.sh ${VERSION} ${INCREMENT_TYPE}) && \
	echo "Version: ${VERSION}" && \
	echo "New Version: $${new_version}" && \
	echo "Incrementing version $$version to $$new_version" && \
 	echo $$new_version > VERSION && \
	$(GIT) commit -am"[${PROJECT}] increment and release version to [${INCREMENT_TYPE}] $${new_version}" && \
	$(GIT) push origin master

releaseConfigDryRun: clean
	$(GRADLE) jreleaserConfig

releaseMavenRelease: releaseVersionIncrement
	$(GRADLE) jreleaserRelease

releaseMavenCentral: releaseVersionIncrement
	$(GRADLE) jreleaserDeploy

