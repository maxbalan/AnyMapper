#!/bin/bash

# Increment version based on the type (MAJOR, MINOR, PATCH)
version=$1
increment_type=$2

# echo "${version}-${increment_type}"

IFS='.' read -r -a version_parts <<< $version

# echo "${version_parts[0]}"

# Increment version based on type
case "$increment_type" in
    MAJOR)
        version_parts[0]=$((version_parts[0] + 1))
        version_parts[1]=0
        version_parts[2]=0
        ;;
    MINOR)
        version_parts[1]=$((version_parts[1] + 1))
        version_parts[2]=0
        ;;
    PATCH)
        version_parts[2]=$((version_parts[2] + 1))
        ;;
    *)
        echo "Invalid increment type: $increment_type"
        exit 1
        ;;
esac

# Output the incremented version
echo "${version_parts[0]}.${version_parts[1]}.${version_parts[2]}"
