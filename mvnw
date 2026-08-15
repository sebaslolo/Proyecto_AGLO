#!/usr/bin/env sh
# Maven Wrapper (only-script distribution).
set -eu

base_dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
properties_file="$base_dir/.mvn/wrapper/maven-wrapper.properties"

if [ ! -r "$properties_file" ]; then
    echo "No se encontró $properties_file" >&2
    exit 1
fi

distribution_url=$(sed -n 's/^distributionUrl=//p' "$properties_file" | head -n 1)
if [ -z "$distribution_url" ]; then
    echo "distributionUrl no está configurada en $properties_file" >&2
    exit 1
fi

archive_name=${distribution_url##*/}
archive_base=${archive_name%-bin.zip}
maven_user_home=${MAVEN_USER_HOME:-"$HOME/.m2"}
wrapper_dir="$maven_user_home/wrapper/dists/$archive_base"
maven_home="$wrapper_dir/$archive_base"

if [ ! -x "$maven_home/bin/mvn" ]; then
    mkdir -p "$wrapper_dir"
    archive_path="$wrapper_dir/$archive_name"
    if [ ! -f "$archive_path" ]; then
        echo "Descargando Maven Wrapper desde $distribution_url" >&2
        if command -v curl >/dev/null 2>&1; then
            curl --fail --location --silent --show-error "$distribution_url" --output "$archive_path"
        elif command -v wget >/dev/null 2>&1; then
            wget --quiet --output-document="$archive_path" "$distribution_url"
        else
            echo "Se requiere curl o wget para descargar Maven." >&2
            exit 1
        fi
    fi

    temporary_dir=$(mktemp -d "$wrapper_dir/.extract.XXXXXX")
    trap 'rm -rf "$temporary_dir"' EXIT HUP INT TERM
    unzip -q "$archive_path" -d "$temporary_dir"
    mv "$temporary_dir/$archive_base" "$maven_home"
    trap - EXIT HUP INT TERM
    rmdir "$temporary_dir" 2>/dev/null || true
fi

exec "$maven_home/bin/mvn" -f "$base_dir/pom.xml" "$@"
