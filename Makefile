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

.PHONY: clean build releaseConfigDryRun releaseMavenCentral
clean:
	$(GRADLE) clean

# application build
.PHONY: build
build: clean
	$(GRADLE) build

releaseConfigDryRun: clean
	$(GRADLE) jreleaserConfig

releaseMavenCentral: clean
	$(GRADLE) jreleaserUpload
