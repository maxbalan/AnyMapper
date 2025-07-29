# MAKEFILE
#
# @author      Maxim
# -----------------------------------------------------------------------------------

PROJECT=anymapper

# --------------------------------CONFIG-----------------------------------------------
#INCREMENT_TYPE
BENCHMARK_REPORT_DIR=resources/benchmark
PYTHON_PLOT_DIR=resources/scripts/python/plot
PYTHON_PLOT_SCRIPT=plot_generator.py

# -----------------------------------CMD-----------------------------------------------
GRADLE=./gradlew
GIT=git
PYTHON=python3

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

.PHONY: clean test benchmarkTest build testBenchmarkPythonInit
clean:
	$(GRADLE) clean

build:
	$(GRADLE) build

test:
	$(GRADLE) test

testBenchmark:
	$(GRADLE) benchmarkTest

testBenchmarkPythonInit:
	$(PYTHON) -m venv $(PYTHON_PLOT_DIR)/.venv
	source $(PYTHON_PLOT_DIR)/.venv/bin/activate && \
	pip3 install -r $(PYTHON_PLOT_DIR)/requirements.txt

testBenchmarkPlot:
	cd $(PYTHON_PLOT_DIR) && \
	$(PYTHON) $(PYTHON_PLOT_SCRIPT) ../../../../$(BENCHMARK_REPORT_DIR)/data/benchmark_map.csv ../../../../$(BENCHMARK_REPORT_DIR)/benchmark_map.png Map && \
	$(PYTHON) $(PYTHON_PLOT_SCRIPT) ../../../../$(BENCHMARK_REPORT_DIR)/data/benchmark_list.csv ../../../../$(BENCHMARK_REPORT_DIR)/benchmark_list.png List

.PHONY: releaseVersionIncrement releaseConfigDryRun releaseMavenCentral
releaseVersionIncrement: build
	if [ -z ${INCREMENT_TYPE} ]; then echo "FATAL: ENV INCREMENT_TYPE must be defined."; exit 2; fi
	if [ -z ${VERSION} ]; then echo "FATAL: ENV VERSION must be defined."; exit 2; fi
	if [ -z ${NEW_VERSION} ]; then echo "FATAL: ENV NEW_VERSION must be defined."; exit 2; fi

	mkdir -p ./target/deploy && \
	echo "Version: ${VERSION}" && \
	echo "New Version: ${NEW_VERSION}" && \
	echo "Incrementing version '${VERSION}' to '${NEW_VERSION}'" && \
	echo "${NEW_VERSION}" > VERSION && \
	$(GRADLE) jreleaserConfig && \
	$(GRADLE) clean && \
	$(GRADLE) publish && \
	$(GRADLE) jreleaserRelease -PpreviousTag=${VERSION} && \
	$(GIT) commit -am"[${INCREMENT_TYPE}] increment and release version [${NEW_VERSION}]" && \
	$(GIT) push origin master && \
	$(GIT) tag "v${NEW_VERSION}" && \
	$(GIT) push --tags

releaseConfigDryRun: clean
	$(GRADLE) jreleaserConfig

releaseMavenCentral:
	$(GRADLE) jreleaserConfig
	$(GRADLE) clean
	$(GRADLE) publish
	$(GRADLE) jreleaserDeploy

